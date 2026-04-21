package com.nageoffer.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.ShortlinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkUpdateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkPageRespDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController

public class ShortlinkController {

    ShortlinkRemoteService shortlinkRemoteService = new ShortlinkRemoteService(){};

    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortlinkPageRespDTO>> pageShortLink(ShortlinkPageReqDTO requestParam) {
        return (shortlinkRemoteService.pageShortLink(requestParam));
    }

    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortlinkCreateRespDTO> createShortlink(@RequestBody ShortlinkCreateReqDTO requestParam) {
        return shortlinkRemoteService.createShortlink(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/group_count")
    public Result<List<ShortlinkGroupCountQueryRespDTO>> groupCount(@RequestParam List<String> requestParam) {
        return shortlinkRemoteService.listGroupCount(requestParam);
    }
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortlink(@RequestBody ShortlinkUpdateReqDTO requestParam) {
        shortlinkRemoteService.updateShortlink(requestParam);

        return Results.success();
    }
}
