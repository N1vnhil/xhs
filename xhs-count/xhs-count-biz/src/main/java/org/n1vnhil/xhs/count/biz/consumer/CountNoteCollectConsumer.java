package org.n1vnhil.xhs.count.biz.consumer;

import com.github.phantomthief.collection.BufferTrigger;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.count.biz.enums.CollectUncollectNoteTypeEnum;
import org.n1vnhil.xhs.count.biz.model.dto.CountCollectUncollectMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_NOTE_COLLECT,
        topic = MQConstants.TOPIC_COUNT_NOTE_COLLECT
)
public class CountNoteCollectConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private BufferTrigger<String> bufferTrigger = BufferTrigger.<String>batchBlocking()
            .bufferSize(50000)
            .batchSize(1000)
            .linger(Duration.ofSeconds(1))
            .setConsumerEx(this::consumeMessage)
            .build();

    @Override
    public void onMessage(String body) {
        bufferTrigger.enqueue(body);
    }

    private void consumeMessage(List<String> bodys) {
        log.info("==> 【笔记收藏数】聚合消息，size: {}", bodys.size());
        log.info("==> 【笔记收藏数】聚合消息, {}", JsonUtils.toJsonString(bodys));

        List<CountCollectUncollectMqDTO> collectUncollectMqDTOS = bodys.stream()
                .map(body -> JsonUtils.parseObject(body, CountCollectUncollectMqDTO.class)).toList();
        Map<Long, List<CountCollectUncollectMqDTO>> groupMap = collectUncollectMqDTOS.stream().collect(Collectors.groupingBy(CountCollectUncollectMqDTO::getNoteId));
        Map<Long, Integer> countMap = Maps.newHashMap();
        for(Map.Entry<Long, List<CountCollectUncollectMqDTO>> entry: groupMap.entrySet()) {
            List<CountCollectUncollectMqDTO> list = entry.getValue();
            int count = 0;
            for(CountCollectUncollectMqDTO countCollectUncollectMqDTO : list) {
                Integer type = countCollectUncollectMqDTO.getType();
                CollectUncollectNoteTypeEnum collectUncollectNoteTypeEnum = CollectUncollectNoteTypeEnum.valueOf(type);
                if(Objects.isNull(collectUncollectNoteTypeEnum)) continue;
                else if(Objects.equals(collectUncollectNoteTypeEnum, CollectUncollectNoteTypeEnum.COLLECT)) {
                    count++;
                } else if(Objects.equals(collectUncollectNoteTypeEnum, CollectUncollectNoteTypeEnum.UNCOLLECT)) {
                    count--;
                }
            }
            countMap.put(entry.getKey(), count);
        }

        log.info("## 【笔记收藏数】聚合后计数数据：{}", JsonUtils.toJsonString(countMap));

        countMap.forEach((k, v) -> {
            String redisKey = RedisKeyConstants.buildCountNoteKey(k);
            boolean existed = redisTemplate.hasKey(redisKey);
            if(existed) {
                redisTemplate.opsForHash().increment(redisKey, RedisKeyConstants.FIELD_COLLECT_TOTAL, v);
            }
        });

        Message<String> message = MessageBuilder.withPayload(JsonUtils.toJsonString(countMap)).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_COUNT_NOTE_COLLECT_2_DB, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==> 【计数服务：笔记收藏数落库】MQ发送成功，SendResult: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==> 【计数服务：笔记收藏数落库】MQ发送异常，", throwable);
            }
        });
    }

}

