package com.nageoffer.shortlink.admin.controller;


import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.UrlTitleRemoteService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlTitleController {

    UrlTitleRemoteService urlTitleRemoteService = new UrlTitleRemoteService(){};

    @GetMapping("api/short-link/admin/v1/title")
    public Result<String> getUrlTitle(@RequestParam("url") String url){
        return urlTitleRemoteService.getUrlTitle(url);
    }
}
