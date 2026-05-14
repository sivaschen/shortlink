package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import io.lettuce.core.dynamic.annotation.Param;

public interface ShortlinkMapper extends BaseMapper<ShortlinkDO> {

    /**
     * 分页统计短链接
     */
    IPage<ShortlinkDO> pageLink(ShortlinkPageReqDTO requestParam);

    /**
     * 短链接访问统计自增
     */
    void incrementStats(@Param("gid") String gid,
                        @Param("fullShortUrl") String fullShortUrl,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalUip") Integer totalUip);
}
