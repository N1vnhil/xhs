package org.n1vnhil.xhs.count.biz.consumer;

import com.github.phantomthief.collection.BufferTrigger;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.count.biz.enums.LikeUnlikeNoteTypeEnum;
import org.n1vnhil.xhs.count.biz.model.dto.CountLikeUnlikeNoteMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
        Map<Long, Integer> countMap = new HashMap<>();
        for(Map.Entry<Long, List<CountLikeUnlikeNoteMqDTO>> entry: groupByNoteId.entrySet()) {
            List<CountLikeUnlikeNoteMqDTO> list = entry.getValue();
            int finalCount = 0;
            for(CountLikeUnlikeNoteMqDTO countLikeUnlikeNoteMqDTO: list) {
                Integer type = countLikeUnlikeNoteMqDTO.getType();
                LikeUnlikeNoteTypeEnum likeUnlikeNoteTypeEnum = LikeUnlikeNoteTypeEnum.valueOf(type);
                if(Objects.isNull(likeUnlikeNoteTypeEnum)) continue;

                switch (likeUnlikeNoteTypeEnum) {
                    case LIKE -> finalCount++;
                    case UNLIKE -> finalCount--;
                }
            }

            countMap.put(entry.getKey(), finalCount);
        }

        log.info("==========> 【笔记点赞数】聚合后计数数据：{}", countMap.toString());

        // 更新 redis
        countMap.forEach((k, v) -> {
            String redisKey = RedisKeyConstants.buildCountUserKey(k);
            if(redisTemplate.hasKey(redisKey)) {
                redisTemplate.opsForHash().increment(redisKey, RedisKeyConstants.FIELD_LIKE_TOTAL, v);
            }
        });
    }

}
