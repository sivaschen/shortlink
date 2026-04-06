package com.nageoffer.shortlink.project.service.ShortlinkServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.config.RBloomFilterConfiguration;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortlinkMapper;
import com.nageoffer.shortlink.project.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import com.nageoffer.shortlink.project.toolkit.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class ShortlinkServiceImpl extends ServiceImpl<ShortlinkMapper, ShortlinkDO> implements ShortlinkService {
    private final RBloomFilter<String> shortUrlCreateCachePenetrationBloomFilter;

    @Override
    public ShortlinkCreateRespDTO createShortlink(ShortlinkCreateReqDTO reqDTO) {
        String suffix = generateSuffix(reqDTO);
        String fullShortUrl = reqDTO.getDomain() + "/" + suffix;
        ShortlinkDO shortlinkDO = BeanUtil.toBean(reqDTO, ShortlinkDO.class);
        shortlinkDO.setFullShortUrl(reqDTO.getDomain() + "/" +suffix );
        shortlinkDO.setShortUri(suffix);
        shortlinkDO.setEnableStatus(0);

        try {
            baseMapper.insert(shortlinkDO);

        } catch (DuplicateKeyException e) {

            LambdaQueryWrapper<ShortlinkDO> wrapper = Wrappers.lambdaQuery(ShortlinkDO.class)
                    .eq(ShortlinkDO::getFullShortUrl, fullShortUrl);
            ShortlinkDO hasShortlink = baseMapper.selectOne(wrapper);

            if(hasShortlink != null) {
                log.warn("短链接，{} 重复入库",fullShortUrl);

                throw new ServiceException("短链接生成重复");

            }
        }
        shortUrlCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortlinkCreateRespDTO.builder()
                .fullShortUrl(shortlinkDO.getFullShortUrl())
                .gid(reqDTO.getGid())
                .originalUrl(reqDTO.getOriginUrl())
                .build();
    }

    @Override
    public IPage<ShortlinkPageRespDTO> pageShorlink(ShortlinkPageReqDTO requestParam) {

        LambdaQueryWrapper<ShortlinkDO> wrapper = Wrappers.lambdaQuery(ShortlinkDO.class).eq(ShortlinkDO::getGid, requestParam.getGid()).eq(ShortlinkDO::getDelFlag, 0)
                .eq(ShortlinkDO::getEnableStatus, 0).orderByDesc(ShortlinkDO::getCreateTime);

        IPage<ShortlinkDO> resultPage = baseMapper.selectPage(requestParam,wrapper);


        return resultPage.convert(item -> BeanUtil.toBean(item, ShortlinkPageRespDTO.class));
    }

    private String generateSuffix(ShortlinkCreateReqDTO reqDTO){
        int generateCount = 0;
        String originalUrl = reqDTO.getOriginUrl();
        String shortUrl;
        while(true) {
            if(generateCount == 10) {
                throw new ServiceException("短链接频繁生产，请稍后重试");
            };
            originalUrl+=originalUrl+System.currentTimeMillis();
            shortUrl = HashUtil.hashToBase62(originalUrl);
            if(shortUrlCreateCachePenetrationBloomFilter.contains(reqDTO.getDomain() + "/" + shortUrl)){
                System.out.println("重新生成中");
                generateCount++;
            } else {

                break;
            }
        }

        return shortUrl;
    }
}
