package org.n1vnhil.xhs.count.biz.consumer;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
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
import org.n1vnhil.xhs.count.biz.model.dto.AggregationCountCollectUncollectNoteMqDTO;
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
        List<AggregationCountCollectUncollectNoteMqDTO> countList = Lists.newArrayList();

        if(groupMap.isEmpty()) return;

        for(Map.Entry<Long, List<CountCollectUncollectMqDTO>> entry: groupMap.entrySet()) {
            List<CountCollectUncollectMqDTO> list = entry.getValue();
            int count = 0;
            Long noteId = entry.getKey();
            Long userId = null;
            for(CountCollectUncollectMqDTO countCollectUncollectMqDTO : list) {
                userId = countCollectUncollectMqDTO.getUserId();
                Integer type = countCollectUncollectMqDTO.getType();
                CollectUncollectNoteTypeEnum collectUncollectNoteTypeEnum = CollectUncollectNoteTypeEnum.valueOf(type);
                if(Objects.isNull(collectUncollectNoteTypeEnum)) continue;
                else if(Objects.equals(collectUncollectNoteTypeEnum, CollectUncollectNoteTypeEnum.COLLECT)) {
                    count++;
                } else if(Objects.equals(collectUncollectNoteTypeEnum, CollectUncollectNoteTypeEnum.UNCOLLECT)) {
                    count--;
                }
            }
            countList.add(AggregationCountCollectUncollectNoteMqDTO.builder()
                            .noteId(noteId)
                            .creatorId(userId)
                            .count(count)
                    .build());
        }

        log.info("## 【笔记收藏数】聚合后计数数据：{}", JsonUtils.toJsonString(countList));

        countList.forEach(o -> {
            Long userId = o.getCreatorId();
            Long noteId = o.getNoteId();
            Integer count = o.getCount();

            String redisUserKey = RedisKeyConstants.buildCountUserKey(userId);
            String redisNoteKey = RedisKeyConstants.buildCountNoteKey(noteId);

            boolean noteExisted = redisTemplate.hasKey(redisNoteKey);
            if(noteExisted) redisTemplate.opsForHash().increment(redisNoteKey, RedisKeyConstants.FIELD_COLLECT_TOTAL, count);

            boolean userExisted = redisTemplate.hasKey(redisUserKey);
            if(userExisted) redisTemplate.opsForHash().increment(redisUserKey, RedisKeyConstants.FIELD_COLLECT_TOTAL, count);
        });

        Message<String> message = MessageBuilder.withPayload(JsonUtils.toJsonString(countList)).build();
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

