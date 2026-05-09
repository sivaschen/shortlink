package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkNetworkStatsDO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {

    @Insert("INSERT INTO t_link_network_stats (full_short_url, gid,date,cnt,network,create_time,update_time,del_flag) VALUES( #{linkNetworkStatsDO.fullShortUrl}, #{linkNetworkStatsDO.gid}, #{linkNetworkStatsDO.date},#{linkNetworkStatsDO.cnt}, #{linkNetworkStatsDO.network},NOW(),NOW(),0) ON DUPLICATE KEY UPDATE cnt = cnt + #{linkNetworkStatsDO.cnt};")
    void uploadLinkNetworkStats(@Param("linkNetworkStatsDO") LinkNetworkStatsDO linkNetworkStatsDO);


    /**
     * 根据短链接获取指定日期内访问网络监控数据
     */
    @Select("SELECT " +
            "    tlns.network, " +
            "    SUM(tlns.cnt) AS cnt " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_network_stats tlns ON tl.full_short_url = tlns.full_short_url " +
            "WHERE " +
            "    tlns.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = #{param.enableStatus} " +
            "    AND tlns.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlns.full_short_url, tl.gid, tlns.network;")
    List<LinkNetworkStatsDO> listNetworkStatsByShortLink(@Param("param") ShortlinkStatsReqDTO requestParam);
}
