package com.nageoffer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.toolkit.RandomStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {



    @Override
    public void saveGroup(String groupName) {
        String gid;


        do{
            gid = RandomStringUtil.generate6CharString();
        } while (!gidAvailable(gid));

        GroupDO groupDO = GroupDO.builder().gid(gid).name(groupName).build();

        baseMapper.insert(groupDO);
    }

    private boolean gidAvailable(String gid) {
        LambdaQueryWrapper<GroupDO> wrapper =  Wrappers.lambdaQuery(GroupDO.class).eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, null);

        GroupDO groupDo = baseMapper.selectOne(wrapper);
        return groupDo == null;
    }
}
