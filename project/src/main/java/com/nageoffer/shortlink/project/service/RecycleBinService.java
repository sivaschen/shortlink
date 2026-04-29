package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkRecyleBinPageReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;

public interface RecycleBinService extends IService<ShortlinkDO>  {



    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);



    IPage<ShortlinkPageRespDTO> recycleBinPageShortlink(ShortlinkRecyleBinPageReqDTO requestParam);

}
