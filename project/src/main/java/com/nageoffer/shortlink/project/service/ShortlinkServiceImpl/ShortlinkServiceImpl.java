package com.nageoffer.shortlink.project.service.ShortlinkServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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
import com.nageoffer.shortlink.project.dao.entity.*;
import com.nageoffer.shortlink.project.dao.mapper.*;
import com.nageoffer.shortlink.project.dto.req.ShortlinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkGroupCountQueryReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortlinkUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortlinkPageRespDTO;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import com.nageoffer.shortlink.project.toolkit.HashUtil;
import com.nageoffer.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.*;
import static com.nageoffer.shortlink.project.common.constant.ShortlinkConstant.AMAP_KEY_URL;


@Slf4j
@RequiredArgsConstructor
@Service
public class ShortlinkServiceImpl extends ServiceImpl<ShortlinkMapper, ShortlinkDO> implements ShortlinkService {
    private final RBloomFilter<String> shortUrlCreateCachePenetrationBloomFilter;
    private final ShortlinkMapper shortlinkMapper;
    private final ShortlinkGotoMapper shortlinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final ShortlinkStatsMapper shortlinkStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOSStatsMapper linkOSStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserMapper;
    private final LinkDeviceStatsMapper linkDeviceMapper;
    private final LinkNetworkStatsMapper linkNetworkMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;

    @Value("${short-link.stats.locale.amap-key}")
    private String amapStatsKey;

    @Value("${short-link.domain.default}")
    private String defaultDomain;

    @Override
    public ShortlinkCreateRespDTO createShortlink(ShortlinkCreateReqDTO reqDTO) {
        String suffix = generateSuffix(reqDTO);
        String fullShortUrl = StrBuilder.create(defaultDomain)
                .append("/")
                .append(suffix).toString();
        ShortlinkDO shortlinkDO = BeanUtil.toBean(reqDTO, ShortlinkDO.class);
        shortlinkDO.setFullShortUrl(defaultDomain + "/" +suffix );
        shortlinkDO.setShortUri(suffix);
        shortlinkDO.setEnableStatus(0);
        shortlinkDO.setFavicon(getUrlIcon(reqDTO.getOriginUrl()));

        ShortlinkGotoDO shortlinkGotoDO = ShortlinkGotoDO.builder()
                .gid(reqDTO.getGid())
                .fullShortUrl(reqDTO.getDomain() + "/" +suffix)
                .build();
        try {
            baseMapper.insert(shortlinkDO);
            shortlinkGotoMapper.insert(shortlinkGotoDO);

        } catch (DuplicateKeyException e) {

            LambdaQueryWrapper<ShortlinkDO> wrapper = Wrappers.lambdaQuery(ShortlinkDO.class)
                    .eq(ShortlinkDO::getFullShortUrl, fullShortUrl);
            ShortlinkDO hasShortlink = baseMapper.selectOne(wrapper);

            if(hasShortlink != null) {
                log.warn("短链接，{} 重复入库",fullShortUrl);

                throw new ServiceException("短链接生成重复");

            }
        }

        stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl), reqDTO.getOriginUrl(), LinkUtil.getLinkCacheValidTime(shortlinkDO.getValidDate()), TimeUnit.MILLISECONDS);
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
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String serverPort = Optional.of(request.getServerPort()).filter(each -> !Objects.equals(each, 80)).
                map(String::valueOf).map(each -> ":" + each).orElse("");
        String fullShortUrl = serverName + serverPort  + "/" + shortUri;

        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        if(StrUtil.isNotBlank(originalLink)){
            shortlinkStats(fullShortUrl,null,  request, response);
            ((HttpServletResponse) response).sendRedirect(originalLink);
            return;
        }

        boolean contains = shortUrlCreateCachePenetrationBloomFilter.contains(fullShortUrl);

        if(!contains){
            ((HttpServletResponse) response).sendRedirect("/page/notfound");

            return;
        };

        String isNullStr = stringRedisTemplate.opsForValue().get(String.format(IS_NULL_GOTO_SHORT_LINK_KEY, fullShortUrl));

        if(StrUtil.isNotBlank(isNullStr)){
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }


        RLock rlock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
            rlock.lock();
            try {

                originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
                if(StrUtil.isNotBlank(originalLink)){
                    ((HttpServletResponse) response).sendRedirect(originalLink);
                    return;
                }
                LambdaQueryWrapper<ShortlinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortlinkGotoDO.class)
                        .eq(ShortlinkGotoDO::getFullShortUrl, fullShortUrl);


                ShortlinkGotoDO shortlinkGotoDO = shortlinkGotoMapper.selectOne(linkGotoQueryWrapper);
                if(shortlinkGotoDO == null) {
                    stringRedisTemplate.opsForValue().set(String.format(IS_NULL_GOTO_SHORT_LINK_KEY,fullShortUrl), "-", 30, TimeUnit.MINUTES);
                    ((HttpServletResponse) response).sendRedirect("/page/notfound");
                    return;
                }

                LambdaQueryWrapper<ShortlinkDO> shortlinkDOWrappers = Wrappers.lambdaQuery(ShortlinkDO.class)
                        .eq(ShortlinkDO::getGid, shortlinkGotoDO.getGid())
                        .eq(ShortlinkDO::getFullShortUrl, fullShortUrl)
                        .eq(ShortlinkDO::getEnableStatus, 0)
                        .eq(ShortlinkDO::getDelFlag, 0);

                ShortlinkDO shortlinkDO = shortlinkMapper.selectOne(shortlinkDOWrappers);
                if(shortlinkDO == null || (shortlinkDO.getValidDate() != null && shortlinkDO.getValidDate().before(new Date())) ) {

                        stringRedisTemplate.opsForValue().set(String.format(IS_NULL_GOTO_SHORT_LINK_KEY,fullShortUrl), "-", 30, TimeUnit.MINUTES);
                        ((HttpServletResponse) response).sendRedirect("/page/notfound");

                        return;
                }
                stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),shortlinkDO.getOriginUrl(), LinkUtil.getLinkCacheValidTime(shortlinkDO.getValidDate()), TimeUnit.MILLISECONDS);
                shortlinkStats(fullShortUrl,shortlinkDO.getGid(),  request, response);
                ((HttpServletResponse) response).sendRedirect(shortlinkDO.getOriginUrl());

            } finally{
                rlock.unlock();
            }




    }
    void shortlinkStats(String fullShortUrl, String gid, ServletRequest request, ServletResponse response) {

        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        AtomicReference<String> uv =  new AtomicReference<>();
        Runnable setCookieTask = () -> {
            uv.set(UUID.fastUUID().toString());
            Cookie cookie = new Cookie("uv", uv.get());
            cookie.setPath(StrUtil.sub(fullShortUrl,fullShortUrl.indexOf("/"), fullShortUrl.length()));
            cookie.setMaxAge(30*24*60*60);
            ((HttpServletResponse) response).addCookie(cookie);
            uvFirstFlag.set(true);
            stringRedisTemplate.opsForSet().add("short-link_uv:stats" + fullShortUrl, uv.get());

        };
        if(ArrayUtil.isNotEmpty(cookies) ){
            Arrays.stream(cookies).filter(item -> Objects.equals(item.getName(), "uv")).findFirst()
                    .map(Cookie::getValue).ifPresentOrElse(each -> {
                        uv.set(each);
                        long added = stringRedisTemplate.opsForSet().add("short-link_uv:stats" + fullShortUrl, each);
                        uvFirstFlag.set(added > 0L);
                        }, setCookieTask);
        } else {
            setCookieTask.run();
        }


        String ip = LinkUtil.getActualIp((HttpServletRequest) request);
        long uipAdded = stringRedisTemplate.opsForSet().add("short-link_uip:stats" + fullShortUrl, ip);
        boolean uipFirstFlag = false;
        if(uipAdded > 0L) {
            uipFirstFlag = true;
        }
        if(StrUtil.isBlank(gid)){
            LambdaQueryWrapper<ShortlinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortlinkGotoDO.class)
                    .eq(ShortlinkGotoDO::getFullShortUrl, fullShortUrl);


            ShortlinkGotoDO shortlinkGotoDO = shortlinkGotoMapper.selectOne(linkGotoQueryWrapper);
            gid = shortlinkGotoDO.getGid();
        }

        int hour = DateUtil.hour(new Date(), true);
        Week week = DateUtil.dayOfWeekEnum(new Date());
        int day = week.getValue();
        ShortlinkStatsDO shortlinkStatsDO = ShortlinkStatsDO
                .builder()
                .gid(gid)
                .hour(hour)
                .weekday(day)
                .pv(1)
                .uv(uvFirstFlag.get() ? 1 : 0)
                .uip(uipFirstFlag ? 1 : 0)
                .fullShortUrl(fullShortUrl)
                .date(new Date())
                .build();
        shortlinkStatsMapper.uploadShortlintStats(shortlinkStatsDO);
        Map<String,Object> map =  new HashMap<>();
        map.put("ip",ip );
        map.put("key", amapStatsKey);
        String stringResult = HttpUtil.get(AMAP_KEY_URL, map);
        JSONObject mapObj = JSON.parseObject(stringResult);
        String infoCode = mapObj.getString("infocode");
        if(StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")){
            String province = mapObj.getString("province");
            boolean localeFlagNull = StringUtil.equals(province, "[]");
            String city = mapObj.getString("city");
            String adcode = mapObj.getString("adcode");
        LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                .gid(gid)
                .date(new Date())
                .cnt(1)
                .city(localeFlagNull ? "未知" : city)
                .country("中国")
                .adcode(localeFlagNull ? "未知" : adcode)
                .province(localeFlagNull ? "未知" : province)
                .fullShortUrl(fullShortUrl)
                .build();
            linkLocaleStatsMapper.uploadLinkLocaleStats(linkLocaleStatsDO);
        };

        LinkOSStatsDO linkOSStatsDO = LinkOSStatsDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .date(new Date())
                .cnt(1)
                .os("mac")
                .build();
        linkOSStatsMapper.uploadLinkOSStats(linkOSStatsDO);

        LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .date(new Date())
                .cnt(1)
                .browser("edge").
                build();
        linkBrowserMapper.uploadLinkBrowserStats(linkBrowserStatsDO);

        LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .date(new Date())
                .cnt(1)
                .device("pc").
                build();
        linkDeviceMapper.uploadLinkDeviceStats(linkDeviceStatsDO);
        LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .date(new Date())
                .cnt(1)
                .network("edge").
                build();
        linkNetworkMapper.uploadLinkNetworkStats(linkNetworkStatsDO);
        LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .ip(ip)
                .user(uv.get())
                .os("mac")
                .network("wifi")
                .device("pc")
                .locale("中国-广东")
                .browser("edge")
                .build();
        linkAccessLogsMapper.insert(linkAccessLogsDO);

    }
    @Override
    public IPage<ShortlinkPageRespDTO> pageShortlink(ShortlinkPageReqDTO requestParam) {

        IPage<ShortlinkDO> resultPage = baseMapper.pageLink(requestParam);

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

    public String getUrlIcon(String pageUrl) {
        try {
            // 1. 模拟浏览器请求，获取文档
            // 设置 User-Agent 防止部分网站拦截非浏览器请求
            // 设置超时时间，避免长时间阻塞
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .get();

            // 2. 优先查找 <link> 标签中的图标
            // 选择器逻辑：查找 head 下 rel 属性包含 "icon" 的 link 标签
            // 常见的 rel 值包括：icon, shortcut icon, apple-touch-icon 等
            Element linkIcon = null;

            // 1. 优先查找标准的 icon 标签 (rel="icon" 或 rel="shortcut icon")
            // 这是一个合法且高效的选择器
            Element iconElement = doc.selectFirst("head > link[rel~=(?i)(shortcut\\s+)?icon]");

            if (iconElement != null) {
                // 2. 获取 href 属性 (使用 abs:href 自动补全为绝对路径)
                String href = iconElement.absUrl("href");

                // 3. 在 Java 代码中校验后缀，避免在选择器中写复杂的正则
                // 只要 href 不为空 且 包含 .ico, .png, .jpg, .jpeg, .svg 其中之一
                if (href != null && !href.isEmpty() && href.matches(".*\\.(ico|png|jpg|jpeg|svg)(\\?.*)?$")) {
                    linkIcon = iconElement;
                }
            }

            // 4. 如果上面没找到，或者找到的没有后缀，尝试查找其他可能的 link 标签 (降级策略)
            if (linkIcon == null) {
                // 获取所有 link 标签，然后在循环中查找包含图片后缀的
                for (Element link : doc.select("head > link[href]")) {
                    String href = link.absUrl("href");
                    if (href.matches(".*\\.(ico|png|jpg|jpeg|svg)(\\?.*)?$")) {
                        linkIcon = link;
                        break;
                    }
                }
            }

            // 5. 最终返回结果
            if (linkIcon != null) {
                String href = linkIcon.absUrl("href");
                if (!href.isEmpty()) {
                    return href;
                }
            }

            // 3. 降级方案：查找 <meta> 标签 (部分网站如微信公众号可能使用 meta)
            // 查找 itemprop="image" 或 property="og:image"
            Element metaIcon = doc.select("head > meta[itemprop=image], head > meta[property=og:image]").first();
            if (metaIcon != null) {
                String content = metaIcon.attr("content");
                if (content != null && !content.isEmpty()) {
                    // meta 标签的 content 属性通常已经是绝对路径，但也需要处理相对路径的情况
                    return resolveUrl(pageUrl, content);
                }
            }

            // 4. 终极兜底：如果 HTML 中完全没定义，通常网站根目录下会有 favicon.ico
            // 例如：https://example.com/favicon.ico
            URL url = new URL(pageUrl);
            return url.getProtocol() + "://" + url.getHost() + "/favicon.ico";

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    };
    private static String resolveUrl(String baseUrl, String relativeUrl) {
        try {
            URL base = new URL(baseUrl);
            URL finalUrl = new URL(base, relativeUrl);
            return finalUrl.toString();
        } catch (MalformedURLException e) {
            return relativeUrl;
        }
    }
}
