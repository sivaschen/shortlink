package com.nageoffer.shortlink.project.service;

import com.nageoffer.shortlink.project.dto.req.ShortlinkStatsReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkStatsRespDTO;

public interface ShortlinkStatsService {

    ShortlinkStatsRespDTO getShortlinkStats(ShortlinkStatsReqDTO requestParam);
}
