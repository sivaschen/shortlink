package com.nageoffer.shortlink.project.controller;


import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.service.UrlTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    private final UrlTitleService urlTitleService;


    @GetMapping("api/short-link/v1/title")
    public Result<String> getUrlTitle(@RequestParam("url") String url) {
        String title = urlTitleService.getUrlTitle(url);

        return Results.success(title);
    }
}
