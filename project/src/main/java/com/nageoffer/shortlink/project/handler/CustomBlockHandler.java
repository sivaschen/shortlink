package com.nageoffer.shortlink.project.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkCreateRespDTO;

public class CustomBlockHandler {
    public static Result<ShortlinkCreateRespDTO> createShortLinkBlockHandlerMethod(ShortlinkCreateReqDTO requestParam, BlockException exception) {
        return new Result<ShortlinkCreateRespDTO>().setCode("B100000").setMessage("当前访问网站人数过多，请稍后再试...");
    }
}
