package org.n1vnhil.xhs.count.biz.consumer;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.domain.mapper.NoteCountDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_NOTE_LIKE_2_DB,
    topic = MQConstants.TOPIC_COUNT_NOTE_LIKE_2_DB
)
public class CountNoteLike2DBConsumer implements RocketMQListener<String> {

    @Autowired
    private NoteCountDOMapper noteCountDOMapper;

    private RateLimiter rateLimiter = RateLimiter.create(5000);

    @Override
    public void onMessage(String body) {
        rateLimiter.acquire();
        log.info("## 消费MQ【点赞落库】, {}...", body);
        Map<Long, Integer> countMap = null;
        try {
            countMap = JsonUtils.parseMap(body, Long.class, Integer.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(CollUtil.isNotEmpty(countMap)) {
            countMap.forEach((k, v) -> noteCountDOMapper.insertOrUpdateLikeTotalById(v, k));
        }
    }
}
