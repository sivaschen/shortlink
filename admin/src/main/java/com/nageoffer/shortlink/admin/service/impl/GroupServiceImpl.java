package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.req.GroupOrderReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.GroupRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.ShortlinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.toolkit.RandomStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortlinkRemoteService shortlinkRemoteService = new ShortlinkRemoteService() {
    };

    @Override
    public void saveGroup(String groupName) {
        String gid;


        do{
            gid = RandomStringUtil.generate6CharString();
        } while (!gidAvailable(gid));

        GroupDO groupDO = GroupDO.builder().gid(gid).username(UserContext.getUsername()).sortOrder(0).name(groupName).build();



        baseMapper.insert(groupDO);
    }

    private boolean gidAvailable(String gid) {
        LambdaQueryWrapper<GroupDO> wrapper =  Wrappers.lambdaQuery(GroupDO.class).eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername());

        GroupDO groupDo = baseMapper.selectOne(wrapper);
        return groupDo == null;
    }

    @Override
    public List<GroupRespDTO> listGroup(){
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0).eq(GroupDO::getUsername, UserContext.getUsername()).orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> listGroupDO = baseMapper.selectList((wrapper));
        Result<List<ShortlinkGroupCountQueryRespDTO>> listResult = shortlinkRemoteService.listGroupCount(listGroupDO.stream().map(GroupDO::getGid).toList());
        List<GroupRespDTO> groupRespDTOS = BeanUtil.copyToList(listGroupDO, GroupRespDTO.class);
        groupRespDTOS.forEach(each -> {

            Optional<ShortlinkGroupCountQueryRespDTO> first = listResult.getData().stream().filter(
                    item -> Objects.equals(item.getGid(), each.getGid())
            ).findFirst();
            first.ifPresent(item -> each.setShortlinkCount(first.get().getShortlinkCount()));
        });

        return groupRespDTOS;
    }

    @Override
    public void updateGroup(ShortLinkUpdateReqDTO updateGroup){
        LambdaUpdateWrapper wrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, updateGroup.getGid());

        GroupDO groupDO = new GroupDO();
        groupDO.setName(updateGroup.getName());
        baseMapper.update(groupDO,wrapper);

    }

    @Override
    public void deleteGroup(String gid){
        LambdaUpdateWrapper wrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag,0)
                .eq(GroupDO::getGid, gid);

        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        System.out.println(groupDO);
        System.out.println(wrapper);
        baseMapper.update(groupDO,wrapper);

    }

    @Override
    public void updateGroupOrder(List<GroupOrderReqDTO> requestParam) {
        requestParam.forEach(item -> {
            GroupDO groupDO = GroupDO.builder().
                    sortOrder(item.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> wrapper = Wrappers.lambdaUpdate(GroupDO.class).eq(GroupDO::getGid, item.getGid()).eq(GroupDO::getUsername, UserContext.getUsername()).eq(GroupDO::getDelFlag, 0);

            baseMapper.update(groupDO,wrapper);



        });
    }
}
