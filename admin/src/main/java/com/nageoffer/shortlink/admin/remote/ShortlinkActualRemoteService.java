package com.nageoffer.shortlink.admin.remote;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortlinkStatsRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.*;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortlinkPageRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FeignClient("short-link-project")
public interface ShortlinkActualRemoteService {



    @PostMapping("/api/short-link/v1/create")
    Result<ShortlinkCreateRespDTO> createShortlink(@RequestBody ShortlinkCreateReqDTO requestParam);

    @GetMapping("/api/short-link/v1/page")
    Result<Page<ShortlinkPageRespDTO>> pageShortLink(
            @RequestParam("gid") String gid,
            @RequestParam("orderTag") String orderTag,
            @RequestParam("current") Long current,
            @RequestParam("size") Long size);


    @GetMapping("api/short-link/v1/group_count")
    Result<List<ShortlinkGroupCountQueryRespDTO>> listGroupCount(@RequestParam("requestParam") List<String> requestParam);


    @PostMapping("/api/short-link/v1/update")
    void updateShortlink(@RequestBody ShortlinkUpdateReqDTO requestParam);

    @GetMapping("api/short-link/v1/recycle-bin/page")
    Result<Page<ShortlinkPageRespDTO>> recycleBinPageShortLink(
        @RequestParam("gids") List<String> gid,
        @RequestParam("current") Long current,
        @RequestParam("size") Long size);

    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    void recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam);

    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    void removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam);

    @PostMapping("/api/short-link/v1/recycle-bin/save")
    void saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam);

    @GetMapping("/api/short-link/v1/title")
    Result<String> getUrlTitle(@RequestParam("url") String url);

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 短链接批量创建响应
     */
//    @PostMapping("/api/short-link/v1/create/batch")
//    Result<ShortlinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortlinkBatchCreateReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内监控数据
     *
     * @param fullShortUrl 完整短链接
     * @param gid          分组标识
     * @param startDate    开始时间
     * @param endDate      结束时间
     * @return 短链接监控信息
     */
    @GetMapping("/api/short-link/v1/stats")
    Result<ShortlinkStatsRespDTO> oneShortLinkStats(@RequestParam("fullShortUrl") String fullShortUrl,
                                                    @RequestParam("gid") String gid,
                                                    @RequestParam("startDate") String startDate,
                                                    @RequestParam("endDate") String endDate);

    /**
     * 访问分组短链接指定时间内监控数据
     *
     * @param gid       分组标识
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 分组短链接监控信息
     */
    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortlinkStatsRespDTO> groupShortLinkStats(@RequestParam("gid") String gid,
                                                      @RequestParam("startDate") String startDate,
                                                      @RequestParam("endDate") String endDate);

    /**
     * 访问单个短链接指定时间内监控访问记录数据
     *
     * @param fullShortUrl 完整短链接
     * @param gid          分组标识
     * @param startDate    开始时间
     * @param endDate      结束时间
     * @return 短链接监控访问记录信息
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(@RequestParam("fullShortUrl") String fullShortUrl,
                                                                               @RequestParam("gid") String gid,
                                                                               @RequestParam("startDate") String startDate,
                                                                               @RequestParam("endDate") String endDate);

    /**
     * 访问分组短链接指定时间内监控访问记录数据
     *
     * @param gid       分组标识
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 分组短链接监控访问记录信息
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(@RequestParam("gid") String gid,
                                                                                    @RequestParam("startDate") String startDate,
                                                                                    @RequestParam("endDate") String endDate);
}
