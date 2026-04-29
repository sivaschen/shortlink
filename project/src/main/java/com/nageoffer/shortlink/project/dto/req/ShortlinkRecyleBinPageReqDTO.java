package com.nageoffer.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import lombok.Data;

import java.util.List;

@Data
public class ShortlinkRecyleBinPageReqDTO extends Page<ShortlinkDO> {

    private List<String> gids;
}
