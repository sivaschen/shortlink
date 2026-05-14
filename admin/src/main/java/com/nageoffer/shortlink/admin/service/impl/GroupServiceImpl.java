package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_GROUP_SAVE_KEY;


@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {


    private final RedissonClient redissonClient;


    @Value("${short-link.group.max-num}")
    private Integer maxGroupNum;

    ShortlinkRemoteService shortlinkRemoteService = new ShortlinkRemoteService() {
    };

    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUsername(), groupName);
    }
    @Override
    public void saveGroup(String username, String groupName) {

        RLock lock = redissonClient.getLock(LOCK_GROUP_SAVE_KEY);


        lock.lock();
        try {

            LambdaQueryWrapper<GroupDO> queryMapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0);
            List<GroupDO> groupDOS = baseMapper.selectList(queryMapper);
            if(CollUtil.isNotEmpty(groupDOS) &&  groupDOS.size() == maxGroupNum) {
                throw new ClientException(String.format("分组数量超过%d", maxGroupNum));
            }
            String gid;
            do{
                gid = RandomStringUtil.generate6CharString();
            } while (!gidAvailable(username,gid));

            GroupDO groupDO = GroupDO.builder().gid(gid).username(username).sortOrder(0).name(groupName).build();
            baseMapper.insert(groupDO);
        } finally {
            lock.unlock();
        }

    }
    private boolean gidAvailable(String username, String gid) {
        LambdaQueryWrapper<GroupDO> wrapper =  Wrappers.lambdaQuery(GroupDO.class).eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, Optional.ofNullable(username).orElse(UserContext.getUsername()));

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
