package com.nageoffer.shortlink.project.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkGroupCountQueryReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortlinkController {
    private final ShortlinkService shortlinkService;

    @PostMapping("/api/short-link/v1/create")
    public Result<ShortlinkCreateRespDTO> createShortlink(@RequestBody ShortlinkCreateReqDTO requestParam) {

        return Results.success(shortlinkService.createShortlink(requestParam));
    }

    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortlinkPageRespDTO>> pageShortLink(ShortlinkPageReqDTO requestParam) {

        return Results.success(shortlinkService.pageShorlink(requestParam));
    }

    @GetMapping("/api/short-link/v1/group_count")
    public Result<List<ShortlinkGroupCountQueryRespDTO>> group_link_count(@RequestParam("requestParam") List<String> requestParam) {
        return Results.success(shortlinkService.groupLinkCount(requestParam));
    }
}
