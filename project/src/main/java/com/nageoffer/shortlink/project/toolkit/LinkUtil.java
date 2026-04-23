package com.nageoffer.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.nageoffer.shortlink.project.common.constant.ShortlinkConstant.DEFAULT_CACHE_VALID_TIME;

public class LinkUtil {


    public static long getLinkCacheValidTime(Date validDate) {
        return Optional.ofNullable(validDate).map(each -> DateUtil.between(new Date(), each, DateUnit.MS)).orElse(DEFAULT_CACHE_VALID_TIME);
    }
}
