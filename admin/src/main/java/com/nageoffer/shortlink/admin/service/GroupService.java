package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dto.req.GroupOrderReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.GroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {


    void saveGroup(String groupName);
    List<GroupRespDTO> listGroup();
    void updateGroup(ShortLinkUpdateReqDTO requestParam);

    void deleteGroup(String gid);
    void updateGroupOrder(List<GroupOrderReqDTO> requestParam);


}
