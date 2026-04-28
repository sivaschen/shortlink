package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.dto.RecycleBinRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.ShortlinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ShortlinkRecycleBinController {

    ShortlinkRemoteService shortlinkRemoteService = new ShortlinkRemoteService() {};

    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveShortlinkRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        shortlinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }
}
