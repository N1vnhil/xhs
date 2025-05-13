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
@RocketMQMessageListener(
        consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_NOTE_COLLECT_2_DB,
        topic = MQConstants.TOPIC_COUNT_NOTE_COLLECT_2_DB
)
public class CountNoteCollect2DBConsumer implements RocketMQListener<String> {

    private RateLimiter rateLimiter = RateLimiter.create(5000);

    @Autowired
    private NoteCountDOMapper noteCountDOMapper;

    @Override
    public void onMessage(String body) {
        rateLimiter.acquire();
        log.info("## 消费 MQ 【计数：笔记收藏落库】, {}", body);
        Map<Long, Integer> countMap = null;
        try {
            countMap = JsonUtils.parseMap(body, Long.class, Integer.class);
        } catch (Exception e) {
            log.error("## JSON 解析异常：", e);
        }

        if(CollUtil.isNotEmpty(countMap)) {
            countMap.forEach((k, v) -> noteCountDOMapper.insertOrUpdateCollectTotalById(v, k));
        }
    }
}
