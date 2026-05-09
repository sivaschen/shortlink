package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.LinkDeviceStatsDO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {

    @Insert("INSERT INTO t_link_device_stats (full_short_url, gid,date,cnt,device,create_time,update_time,del_flag) VALUES( #{linkDeviceStatsDO.fullShortUrl}, #{linkDeviceStatsDO.gid}, #{linkDeviceStatsDO.date},#{linkDeviceStatsDO.cnt}, #{linkDeviceStatsDO.device},NOW(),NOW(),0) ON DUPLICATE KEY UPDATE cnt = cnt + #{linkDeviceStatsDO.cnt};")
    void uploadLinkDeviceStats(@Param("linkDeviceStatsDO") LinkDeviceStatsDO linkDeviceStatsDO);


    /**
     * 根据短链接获取指定日期内访问设备监控数据
     */
    @Select("SELECT " +
            "    tlds.device, " +
            "    SUM(tlds.cnt) AS cnt " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_device_stats tlds ON tl.full_short_url = tlds.full_short_url " +
            "WHERE " +
            "    tlds.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = #{param.enableStatus} " +
            "    AND tlds.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlds.full_short_url, tl.gid, tlds.device;")
    List<LinkDeviceStatsDO> listDeviceStatsByShortLink(@Param("param") ShortlinkStatsReqDTO requestParam);
}
