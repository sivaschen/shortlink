package com.nageoffer.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.ShortlinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class ShortlinkController {



    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortlinkPageRespDTO>> pageShortLink(ShortlinkPageReqDTO requestParam) {
        ShortlinkRemoteService shortlinkRemoteService = new ShortlinkRemoteService(){};
        return (shortlinkRemoteService.pageShortLink(requestParam));
    }

    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortlinkCreateRespDTO> createShortlink(@RequestBody ShortlinkCreateReqDTO requestParam) {
        ShortlinkRemoteService shortlinkRemoteService = new ShortlinkRemoteService(){};
        return shortlinkRemoteService.createShortlink(requestParam);
    }
}
