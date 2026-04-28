package com.nageoffer.shortlink.project.service.ShortlinkServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortlinkMapper;
import com.nageoffer.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;
import com.nageoffer.shortlink.project.service.RecycleBinService;
import com.nageoffer.shortlink.project.toolkit.LinkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;


@Slf4j
@RequiredArgsConstructor
@Service
public class RecycleBinServiceImpl extends ServiceImpl<ShortlinkMapper, ShortlinkDO> implements RecycleBinService {

    private final ShortlinkMapper shortlinkMapper;
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortlinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortlinkDO.class)
                .eq(ShortlinkDO::getGid, requestParam.getGid())
                .eq(ShortlinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortlinkDO::getEnableStatus, 0)
                .eq(ShortlinkDO::getDelFlag, 0);

        ShortlinkDO shortlinkDO = ShortlinkDO.builder().
        enableStatus(1).build();
        baseMapper.update(shortlinkDO, updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, requestParam.getFullShortUrl()));



    }

    @Override
    public IPage<ShortlinkPageRespDTO> recycleBinPageShortlink(ShortlinkPageReqDTO requestParam) {

        LambdaQueryWrapper<ShortlinkDO> wrapper = Wrappers.lambdaQuery(ShortlinkDO.class)
                .eq(ShortlinkDO::getGid, requestParam.getGid()).eq(ShortlinkDO::getDelFlag, 0)
                .eq(ShortlinkDO::getEnableStatus, 1).orderByDesc(ShortlinkDO::getCreateTime);

        IPage<ShortlinkDO> resultPage = baseMapper.selectPage(requestParam,wrapper);


        return resultPage.convert(item -> BeanUtil.toBean(item, ShortlinkPageRespDTO.class));
    }
}
