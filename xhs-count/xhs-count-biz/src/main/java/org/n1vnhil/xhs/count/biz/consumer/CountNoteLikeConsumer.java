package org.n1vnhil.xhs.count.biz.consumer;

import com.github.phantomthief.collection.BufferTrigger;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.count.biz.enums.LikeUnlikeNoteTypeEnum;
import org.n1vnhil.xhs.count.biz.model.dto.AggregationCountLikeUnlikeNoteMqDTO;
import org.n1vnhil.xhs.count.biz.model.dto.CountLikeUnlikeNoteMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_NOTE_LIKE,
    topic = MQConstants.TOPIC_COUNT_NOTE_LIKE)
public class CountNoteLikeConsumer implements RocketMQListener<String> {

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
        log.info("==> 【笔记点赞数】聚合消息，size: {}", bodys.size());
        log.info("==> 【笔记点赞数】聚合消息，{}", JsonUtils.toJsonString(bodys));

        List<CountLikeUnlikeNoteMqDTO> countLikeUnlikeNoteMqDTOS = bodys.stream()
                .map(body -> JsonUtils.parseObject(body, CountLikeUnlikeNoteMqDTO.class)).toList();
        Map<Long, List<CountLikeUnlikeNoteMqDTO>> groupByNoteId = countLikeUnlikeNoteMqDTOS.stream()
                .collect(Collectors.groupingBy(CountLikeUnlikeNoteMqDTO::getNoteId));
        List<AggregationCountLikeUnlikeNoteMqDTO> countList = Lists.newArrayList();
        for(Map.Entry<Long, List<CountLikeUnlikeNoteMqDTO>> entry: groupByNoteId.entrySet()) {
            List<CountLikeUnlikeNoteMqDTO> list = entry.getValue();
            int finalCount = 0;
            Long noteId = entry.getKey();
            Long creatorId = null;
            for(CountLikeUnlikeNoteMqDTO countLikeUnlikeNoteMqDTO: list) {
                creatorId = countLikeUnlikeNoteMqDTO.getNoteCreatorId();
                Integer type = countLikeUnlikeNoteMqDTO.getType();
                LikeUnlikeNoteTypeEnum likeUnlikeNoteTypeEnum = LikeUnlikeNoteTypeEnum.valueOf(type);
                if(Objects.isNull(likeUnlikeNoteTypeEnum)) continue;

                switch (likeUnlikeNoteTypeEnum) {
                    case LIKE -> finalCount++;
                    case UNLIKE -> finalCount--;
                }
            }

            countList.add(AggregationCountLikeUnlikeNoteMqDTO.builder()
                            .noteId(noteId)
                            .creatorId(creatorId)
                            .count(finalCount)
                .build());
        }

        log.info("==========> 【笔记点赞数】聚合后计数数据：{}", countList.toString());

        // 更新 redis
        countList.forEach(o -> {
            Long creatorId = o.getCreatorId();
            Long noteId = o.getNoteId();
            Integer count = o.getCount();
            String countNoteRedisKey = RedisKeyConstants.buildCountNoteKey(noteId);
            String countUserRedisKey = RedisKeyConstants.buildCountUserKey(creatorId);

            boolean countNoteExisted = redisTemplate.hasKey(countNoteRedisKey);
            if(countNoteExisted) {
                redisTemplate.opsForHash().increment(countNoteRedisKey, RedisKeyConstants.FIELD_LIKE_TOTAL, count);
            }

            boolean userNoteExisted = redisTemplate.hasKey(countUserRedisKey);
            if(userNoteExisted) {
                redisTemplate.opsForHash().increment(countUserRedisKey, RedisKeyConstants.FIELD_LIKE_TOTAL, count);
            }
        });

        // 发送MQ计数落库
        Message<String> message = MessageBuilder.withPayload(JsonUtils.toJsonString(countList)).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_COUNT_NOTE_LIKE_2_DB, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==========>【点赞落库】MQ发送成功，sendResult: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==========>【点赞落库】MQ发送异常", throwable);
            }
        });
    }

}
