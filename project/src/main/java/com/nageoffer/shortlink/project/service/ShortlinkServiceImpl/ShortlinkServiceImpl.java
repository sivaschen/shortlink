package com.nageoffer.shortlink.project.service.ShortlinkServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.common.enums.ValidDateType;
import com.nageoffer.shortlink.project.config.RBloomFilterConfiguration;
import com.nageoffer.shortlink.project.dao.entity.ShortlinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortlinkMapper;
import com.nageoffer.shortlink.project.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkGroupCountQueryReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import com.nageoffer.shortlink.project.toolkit.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@Service
public class ShortlinkServiceImpl extends ServiceImpl<ShortlinkMapper, ShortlinkDO> implements ShortlinkService {
    private final RBloomFilter<String> shortUrlCreateCachePenetrationBloomFilter;
    private final ShortlinkMapper shortlinkMapper;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortlink(ShortlinkUpdateReqDTO requestParam) {
        //修改gid需要删除后重新插入
        LambdaQueryWrapper<ShortlinkDO> queryWrapper = Wrappers.lambdaQuery(ShortlinkDO.class)
                .eq(ShortlinkDO::getGid, requestParam.getGid())
                .eq(ShortlinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortlinkDO::getDelFlag, 0)
                .eq(ShortlinkDO::getEnableStatus, 0);

        ShortlinkDO hasShortlinkDO = baseMapper.selectOne(queryWrapper);

        if(hasShortlinkDO == null) {
            throw new ClientException("短链接记录不存在");
        };

        ShortlinkDO shortlinkDO = ShortlinkDO.builder()
                .domain(hasShortlinkDO.getDomain())
                .shortUri(hasShortlinkDO.getShortUri())
                .clickNum(hasShortlinkDO.getClickNum())
                .favicon(hasShortlinkDO.getFavicon())
                .gid(requestParam.getGid())
                .createdType(hasShortlinkDO.getCreatedType())
                .describe(requestParam.getDescribe())
                .originUrl(requestParam.getOriginUrl())
                .validDate(requestParam.getValidDate())
                .validDateType(requestParam.getValidDateType())
                .build();


        if(Objects.equals(hasShortlinkDO.getGid(),requestParam.getGid())) {
            LambdaUpdateWrapper<ShortlinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortlinkDO.class)
                    .eq(ShortlinkDO::getGid, requestParam.getGid())
                    .eq(ShortlinkDO::getDelFlag, 0)
                    .eq(ShortlinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortlinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(),ValidDateType.PERMANENT.getType()), ShortlinkDO::getValidDate, null);
            baseMapper.update(shortlinkDO, updateWrapper);

        } else {
            LambdaUpdateWrapper<ShortlinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortlinkDO.class)
                    .eq(ShortlinkDO::getGid, hasShortlinkDO.getGid())
                    .eq(ShortlinkDO::getDelFlag, 0)
                    .eq(ShortlinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortlinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortlinkDO);
        }

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
    };

    @Override
    public List<ShortlinkGroupCountQueryRespDTO> groupLinkCount(List<String> requestParam) {
        QueryWrapper<ShortlinkDO> queryWrapper = Wrappers.query(new ShortlinkDO())
                .select("gid as gid, count(*) as shortlinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");

        List<Map<String,Object>> shortlinkDOList = baseMapper.selectMaps(queryWrapper);

        return BeanUtil.copyToList(shortlinkDOList, ShortlinkGroupCountQueryRespDTO.class);
    }
}
