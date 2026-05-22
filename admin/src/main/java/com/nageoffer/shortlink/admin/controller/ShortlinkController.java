package com.nageoffer.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.ShortlinkActualRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkUpdateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ShortlinkController {

    private final ShortlinkActualRemoteService shortlinkActualRemoteService;
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<Page<ShortlinkPageRespDTO>> pageShortLink(ShortlinkPageReqDTO requestParam) {
        return shortlinkActualRemoteService.pageShortLink(
                requestParam.getGid(),
                requestParam.getOrderTag(),
                requestParam.getCurrent(),
                requestParam.getSize()
        );
    }

    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortlinkCreateRespDTO> createShortlink(@RequestBody ShortlinkCreateReqDTO requestParam) {
        return shortlinkActualRemoteService.createShortlink(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/group_count")
    public Result<List<ShortlinkGroupCountQueryRespDTO>> groupCount(@RequestParam List<String> requestParam) {
        return shortlinkActualRemoteService.listGroupCount(requestParam);
    }
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortlink(@RequestBody ShortlinkUpdateReqDTO requestParam) {
        shortlinkActualRemoteService.updateShortlink(requestParam);
        return Results.success();
    }
}
