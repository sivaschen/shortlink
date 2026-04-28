package com.nageoffer.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.nageoffer.shortlink.admin.common.convention.result.Result;

public interface UrlTitleRemoteService {


    default Result<String> getUrlTitle(String url) {
        String resultStr = HttpUtil.get("http://127.0.0.1:8002/api/short-link/v1/title?url=" + url);
        return JSON.parseObject(resultStr, new TypeReference<>() {
        });
    }
}
