package com.nageoffer.shortlink.project.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import lombok.Data;

@Data
public class ShortlinkPageReqDTO extends Page<ShortlinkDO> {


    private String gid;



}
