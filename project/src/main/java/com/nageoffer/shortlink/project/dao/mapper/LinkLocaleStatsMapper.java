package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkLocaleStatsDO;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkStatsDO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    @Insert("INSERT INTO t_link_locale_stats (full_short_url, gid,date,cnt,province,city,adcode,country,create_time,update_time,del_flag) VALUES( #{linkLocaleStatsDO.fullShortUrl}, #{linkLocaleStatsDO.gid}, #{linkLocaleStatsDO.date},#{linkLocaleStatsDO.cnt}, #{linkLocaleStatsDO.province}, #{linkLocaleStatsDO.city},#{linkLocaleStatsDO.adcode},#{linkLocaleStatsDO.country},NOW(),NOW(),0) ON DUPLICATE KEY UPDATE cnt = cnt + #{linkLocaleStatsDO.cnt};")
    void uploadLinkLocaleStats(@Param("linkLocaleStatsDO") LinkLocaleStatsDO linkLocaleStatsDO);
    /**
     * 根据短链接获取指定日期内地区监控数据
     */
    @Select("SELECT " +
            "    tlls.province, " +
            "    SUM(tlls.cnt) AS cnt " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_locale_stats tlls ON tl.full_short_url = tlls.full_short_url " +
            "WHERE " +
            "    tlls.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = #{param.enableStatus} " +
            "    AND tlls.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlls.full_short_url, tl.gid, tlls.province;")
    List<LinkLocaleStatsDO> listLocaleByShortLink(@Param("param") ShortlinkStatsReqDTO requestParam);


}
