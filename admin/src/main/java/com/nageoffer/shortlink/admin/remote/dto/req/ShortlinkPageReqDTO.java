package com.nageoffer.shortlink.admin.remote.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class ShortlinkPageReqDTO extends Page {


    private String gid;



}
