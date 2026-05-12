package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;

public interface ShortlinkMapper extends BaseMapper<ShortlinkDO> {

    /**
     * 分页统计短链接
     */
    IPage<ShortlinkDO> pageLink(ShortlinkPageReqDTO requestParam);
}
