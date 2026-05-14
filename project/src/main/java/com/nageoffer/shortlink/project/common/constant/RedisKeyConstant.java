package com.nageoffer.shortlink.project.common.constant;

public class RedisKeyConstant {

    public static final String GOTO_SHORT_LINK_KEY = "short-link_goto_%s";

    /**
     * 分布式锁的KEY
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "short-link_lock_goto_%s";
    public static final String IS_NULL_GOTO_SHORT_LINK_KEY = "short-link_is-null_lock_goto_%s";

    /**
     * 短链接修改分组 ID 锁前缀 Key
     */
    public static final String LOCK_GID_UPDATE_KEY = "short-link_lock_update-gid_%s";

    /**
     * 短链接延迟队列消费统计 Key
     */
    public static final String DELAY_QUEUE_STATS_KEY = "short-link_delay-queue:stats";



}
