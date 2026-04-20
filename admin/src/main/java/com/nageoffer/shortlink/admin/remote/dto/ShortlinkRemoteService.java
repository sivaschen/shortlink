package com.nageoffer.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkPageRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ShortlinkRemoteService {

    default Result<IPage<ShortlinkPageRespDTO>> pageShortLink(ShortlinkPageReqDTO requestParam){
        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPgaeStr = HttpUtil.get("http://127.0.0.1:8002/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPgaeStr, new TypeReference<>() {
        });
    }

    default Result<ShortlinkCreateRespDTO> createShortlink(ShortlinkCreateReqDTO requestParam){

        String resultStr = HttpUtil.post("http://127.0.0.1:8002/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultStr, new TypeReference<>() {
        });
    }

    default Result<List<ShortlinkGroupCountQueryRespDTO>> listGroupCount(List<String> requestParam){

        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("requestParam", requestParam);
        String resultStr = HttpUtil.get("http://127.0.0.1:8002/api/short-link/v1/group_count", requestMap);
        return JSON.parseObject(resultStr, new TypeReference<>() {
        });
    }
}
