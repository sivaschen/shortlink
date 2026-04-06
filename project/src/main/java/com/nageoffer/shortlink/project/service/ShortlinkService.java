package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;

public interface ShortlinkService extends IService<ShortlinkDO> {

    public ShortlinkCreateRespDTO createShortlink(ShortlinkCreateReqDTO reqDTO);

    IPage<ShortlinkPageRespDTO> pageShorlink(ShortlinkPageReqDTO requestParam);
}
