package com.nageoffer.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;

public interface RecycleBinRemoteService {

    default void  saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8002/api/short-link/v1/recycle-bin/save", JSON.toJSONString(requestParam));
    }
}
