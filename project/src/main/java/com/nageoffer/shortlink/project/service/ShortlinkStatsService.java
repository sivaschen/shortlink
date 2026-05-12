package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.project.dto.req.ShortlinkStatsAccessRecordByGroupReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkStatsAccessRecordReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkStatsByGroupReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkStatsReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkStatsRespDTO;

import java.util.List;
import java.util.Map;

public interface ShortlinkStatsService {

    ShortlinkStatsRespDTO getShortlinkStats(ShortlinkStatsReqDTO requestParam);


    ShortlinkStatsRespDTO getShortlinkStatsByGroup(ShortlinkStatsByGroupReqDTO requestParam);
    IPage<ShortLinkStatsAccessRecordRespDTO>  getShortlinkStatsAccessRecord(ShortlinkStatsAccessRecordReqDTO requestParam);

    IPage<ShortLinkStatsAccessRecordRespDTO>  getShortlinkStatsAccessRecordByGroup(ShortlinkStatsAccessRecordByGroupReqDTO requestParam);


}
