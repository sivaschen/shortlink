package com.nageoffer.shortlink.project.service.ShortlinkServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortlinkMapper;
import com.nageoffer.shortlink.project.dto.req.*;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;
import com.nageoffer.shortlink.project.service.RecycleBinService;
import com.nageoffer.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.IS_NULL_GOTO_SHORT_LINK_KEY;


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
    public IPage<ShortlinkPageRespDTO> recycleBinPageShortlink(ShortlinkRecyleBinPageReqDTO requestParam) {

        LambdaQueryWrapper<ShortlinkDO> wrapper = Wrappers.lambdaQuery(ShortlinkDO.class)
                .in(ShortlinkDO::getGid, requestParam.getGids()).eq(ShortlinkDO::getDelFlag, 0)
                .eq(ShortlinkDO::getEnableStatus, 1).orderByDesc(ShortlinkDO::getUpdateTime);

        IPage<ShortlinkDO> resultPage = baseMapper.selectPage(requestParam,wrapper);


        return resultPage.convert(item -> BeanUtil.toBean(item, ShortlinkPageRespDTO.class));
    }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        LambdaUpdateWrapper<ShortlinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortlinkDO.class)
                .eq(ShortlinkDO::getGid, requestParam.getGid())
                .eq(ShortlinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortlinkDO::getEnableStatus, 1)
                .eq(ShortlinkDO::getDelFlag, 0);

        ShortlinkDO shortlinkDO = ShortlinkDO.builder().
                enableStatus(0).build();
        baseMapper.update(shortlinkDO, updateWrapper);
        stringRedisTemplate.delete(String.format(IS_NULL_GOTO_SHORT_LINK_KEY,requestParam.getFullShortUrl()));

    }

    @Override
    public void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortlinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortlinkDO.class)
                .eq(ShortlinkDO::getGid, requestParam.getGid())
                .eq(ShortlinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortlinkDO::getEnableStatus, 0)
                .eq(ShortlinkDO::getDelFlag, 0);

        baseMapper.delete(updateWrapper);
    }
}
