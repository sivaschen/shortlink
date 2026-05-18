package com.nageoffer.shortlink.project.mq.consumer;

import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.dto.biz.ShortlinkStatsRecordDTO;
import com.nageoffer.shortlink.project.mq.idempotent.MessageQueueIdempotent;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.DELAY_QUEUE_STATS_KEY;


@Slf4j
@Component
@RequiredArgsConstructor
public class DelayShortlinkStatsConsumer implements InitializingBean {

    private final RedissonClient redissonClient;
    private final ShortlinkService shortLinkService;
    private final MessageQueueIdempotent messageQueueIdempotent;

    public void onMessage() {
        Executors.newSingleThreadExecutor(
                        runnable -> {
                            Thread thread = new Thread(runnable);
                            thread.setName("delay_short-link_stats_consumer");
                            thread.setDaemon(Boolean.TRUE);
                            return thread;
                        })
                .execute(() -> {
                    RBlockingDeque<ShortlinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(DELAY_QUEUE_STATS_KEY);
                    RDelayedQueue<ShortlinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
                    for (; ; ) {
                        try {
                            ShortlinkStatsRecordDTO statsRecord = delayedQueue.poll();
                            if (statsRecord != null) {
                                if(!messageQueueIdempotent.isMessageProcessed(statsRecord.getKeys())){
                                    if(messageQueueIdempotent.isAccomplished(statsRecord.getKeys())){
                                        return;
                                    }
                                    throw new ServiceException("消息未完成流程，需要消息队列重试");
                                }
                                try {
                                    shortLinkService.shortLinkStats(null, null, statsRecord);
                                } catch (Throwable th) {
                                    messageQueueIdempotent.deleteMessageId(statsRecord.getKeys());
                                    log.error("延迟记录短链接监控消费异常", th);
                                }
                                messageQueueIdempotent.setAccomplished(statsRecord.getKeys());
                                continue;
                            }
                            LockSupport.parkUntil(500);
                        } catch (Throwable ignored) {
                        }
                    }
                });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        onMessage();
    }
}

