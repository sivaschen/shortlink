package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkGroupCountQueryReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.List;

public interface ShortlinkService extends IService<ShortlinkDO> {

    public ShortlinkCreateRespDTO createShortlink(ShortlinkCreateReqDTO reqDTO);
    public void updateShortlink(ShortlinkUpdateReqDTO reqDTO);
    IPage<ShortlinkPageRespDTO> pageShorlink(ShortlinkPageReqDTO requestParam);
    List<ShortlinkGroupCountQueryRespDTO> groupLinkCount(List<String> requestParam);

    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) throws IOException;
}
