package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface ShortlinkStatsMapper extends BaseMapper<ShortlinkStatsDO> {


    @Insert("INSERT INTO t_link_access_stats (full_short_url, gid,date,pv,uv,uip,hour,weekday,create_time,update_time,del_flag) VALUES( #{shortlinkStatsDO.fullShortUrl}, #{shortlinkStatsDO.gid}, #{shortlinkStatsDO.date},#{shortlinkStatsDO.pv}, #{shortlinkStatsDO.uv}, #{shortlinkStatsDO.uip},#{shortlinkStatsDO.hour},#{shortlinkStatsDO.weekday},NOW(),NOW(),0) ON DUPLICATE KEY UPDATE pv = pv + #{shortlinkStatsDO.pv},uv = uv + #{shortlinkStatsDO.uv},uip = uip + #{shortlinkStatsDO.uip};")
    void uploadShortlintStats(@Param("shortlinkStatsDO") ShortlinkStatsDO shortlinkStatsDO);
}
