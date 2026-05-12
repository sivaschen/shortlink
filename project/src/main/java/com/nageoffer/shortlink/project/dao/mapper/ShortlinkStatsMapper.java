package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkStatsDO;
import com.nageoffer.shortlink.project.dto.req.UvTypeReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ShortlinkStatsMapper extends BaseMapper<ShortlinkStatsDO> {


    @Insert("INSERT INTO t_link_access_stats (full_short_url, gid,date,pv,uv,uip,hour,weekday,create_time,update_time,del_flag) VALUES( #{shortlinkStatsDO.fullShortUrl}, #{shortlinkStatsDO.gid}, #{shortlinkStatsDO.date},#{shortlinkStatsDO.pv}, #{shortlinkStatsDO.uv}, #{shortlinkStatsDO.uip},#{shortlinkStatsDO.hour},#{shortlinkStatsDO.weekday},NOW(),NOW(),0) ON DUPLICATE KEY UPDATE pv = pv + #{shortlinkStatsDO.pv},uv = uv + #{shortlinkStatsDO.uv},uip = uip + #{shortlinkStatsDO.uip};")
    void uploadShortlintStats(@Param("shortlinkStatsDO") ShortlinkStatsDO shortlinkStatsDO);


    @Select("<script> " +
            "SELECT " +
            "    user, " +
            "    CASE " +
            "        WHEN MIN(create_time) BETWEEN #{uvTypeParam.startDate} AND #{uvTypeParam.endDate} THEN '新访客' " +
            "        ELSE '老访客' " +
            "    END AS uvType " +
            "FROM " +
            "    t_link_access_logs " +
            "WHERE " +
            "    full_short_url = #{uvTypeParam.fullShortUrl} " +
            "    AND gid = #{uvTypeParam.gid} " +
            "    AND user IN " +
            "    <foreach item='item' index='index' collection='uvTypeParam.userAccessLogsList' open='(' separator=',' close=')'> " +
            "        #{item} " +
            "    </foreach> " +
            "GROUP BY " +
            "    user;" +
            "    </script>"
    )
    List<Map<String, Object>> getUvTypeByUsers(@Param("uvTypeParam") UvTypeReqDTO uvTypeParam );
}
