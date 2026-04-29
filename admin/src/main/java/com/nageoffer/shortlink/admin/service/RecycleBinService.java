package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortlinkRecycleBinPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkPageRespDTO;

public interface RecycleBinService {
    Result<IPage<ShortlinkPageRespDTO>> recycleBinPageShortLink(ShortlinkRecycleBinPageReqDTO requestParam);
}
