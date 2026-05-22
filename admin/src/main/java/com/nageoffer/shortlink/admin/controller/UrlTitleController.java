package com.nageoffer.shortlink.admin.controller;


import com.nageoffer.shortlink.admin.common.convention.result.Result;

import com.nageoffer.shortlink.admin.remote.ShortlinkActualRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    private final ShortlinkActualRemoteService shortlinkActualRemoteService;

    @GetMapping("api/short-link/admin/v1/title")
    public Result<String> getUrlTitle(@RequestParam("url") String url){
        return shortlinkActualRemoteService.getUrlTitle(url);
    }
}
