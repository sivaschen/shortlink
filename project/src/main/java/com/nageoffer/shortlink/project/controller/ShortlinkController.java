package com.nageoffer.shortlink.project.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.*;
import com.nageoffer.shortlink.project.dto.resp.*;
import com.nageoffer.shortlink.project.handler.CustomBlockHandler;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import com.nageoffer.shortlink.project.service.ShortlinkStatsService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortlinkController {
    private final ShortlinkService shortlinkService;
    private final ShortlinkStatsService shortlinkStatsService;

    @GetMapping("/{short-uri}")
    public void restoreUri(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        shortlinkService.restoreUrl(shortUri,request,response);
    }

    @PostMapping("/api/short-link/v1/create")
    @SentinelResource(
            value = "create_short_link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public Result<ShortlinkCreateRespDTO> createShortlink(@RequestBody ShortlinkCreateReqDTO requestParam) {

        return Results.success(shortlinkService.createShortlink(requestParam));
    }

    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortlink(@RequestBody ShortlinkUpdateReqDTO requestParam) {
        shortlinkService.updateShortlink(requestParam);
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortlinkPageRespDTO>> pageShortLink(ShortlinkPageReqDTO requestParam) {

        return Results.success(shortlinkService.pageShortlink(requestParam));
    }

    @GetMapping("/api/short-link/v1/group_count")
    public Result<List<ShortlinkGroupCountQueryRespDTO>> group_link_count(@RequestParam("requestParam") List<String> requestParam) {
        return Results.success(shortlinkService.groupLinkCount(requestParam));
    }

    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortlinkStatsRespDTO> getShortlinkStats(ShortlinkStatsReqDTO requestParam) {
        return Results.success(shortlinkStatsService.getShortlinkStats(requestParam));
    }

    @GetMapping("/api/short-link/v1/stats/group")
    public Result<ShortlinkStatsRespDTO> getGroupShortlinkStats(ShortlinkStatsByGroupReqDTO requestParam) {
        return Results.success(shortlinkStatsService.getShortlinkStatsByGroup(requestParam));
    }

    @GetMapping("/api/short-link/v1/stats/access-reocrd")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> getShortlinkStatsAccessRecord(ShortlinkStatsAccessRecordReqDTO requestParam) {
        return Results.success(shortlinkStatsService.getShortlinkStatsAccessRecord(requestParam));
    }

    @GetMapping("/api/short-link/v1/stats/access-reocrd/group")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> getShortlinkStatsAccessRecordByGroup(ShortlinkStatsAccessRecordByGroupReqDTO requestParam) {
        return Results.success(shortlinkStatsService.getShortlinkStatsAccessRecordByGroup(requestParam));
    }
}
