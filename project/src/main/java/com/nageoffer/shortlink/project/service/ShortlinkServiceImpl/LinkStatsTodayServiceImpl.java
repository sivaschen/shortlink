package com.nageoffer.shortlink.project.service.ShortlinkServiceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.dao.entity.LinkTodayStatsDO;
import com.nageoffer.shortlink.project.dao.mapper.LinkTodayStatsMapper;
import com.nageoffer.shortlink.project.service.LinkStatsTodayService;
import org.springframework.stereotype.Service;

/**
 * 短链接今日统计接口实现层
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：link）获取项目资料
 */
@Service
public class LinkStatsTodayServiceImpl extends ServiceImpl<LinkTodayStatsMapper, LinkTodayStatsDO> implements LinkStatsTodayService {
}
