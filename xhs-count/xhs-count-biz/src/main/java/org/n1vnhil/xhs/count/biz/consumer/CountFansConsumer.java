package org.n1vnhil.xhs.count.biz.consumer;

import com.github.phantomthief.collection.BufferTrigger;
import com.google.protobuf.MapEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.count.biz.enums.FollowUnfollowTypeEnum;
import org.n1vnhil.xhs.count.biz.model.dto.CountFollowUnfollowMqDTO;
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
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_FANS,
        topic = MQConstants.TOPIC_COUNT_FANS)
public class CountFansConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private BufferTrigger<String> bufferTrigger = BufferTrigger.<String>batchBlocking()
            .bufferSize(50000) // 缓存队列的最大容量
            .batchSize(1000)   // 一批次最多聚合 1000 条
            .linger(Duration.ofSeconds(1)) // 多久聚合一次
            .setConsumerEx(this::consumeMessage)
            .build();

    @Override
    public void onMessage(String body) {
        bufferTrigger.enqueue(body);
    }

    public void consumeMessage(List<String> bodys) {
        log.info("==========> 聚合消息，size: {}", bodys.size());
        log.info("==========> 聚合消息，{}", JsonUtils.toJsonString(bodys));

        List<CountFollowUnfollowMqDTO> countFollowUnfollowMqDTOS = bodys.stream()
                .map(body -> JsonUtils.parseObject(body, CountFollowUnfollowMqDTO.class)).toList();

        // 按目标用户分组
        Map<Long, List<CountFollowUnfollowMqDTO>> targetUserId2CountFollowUnfollowMqDTOs = countFollowUnfollowMqDTOS.stream()
                .collect(Collectors.groupingBy(CountFollowUnfollowMqDTO::getTargetUserId));

        Map<Long, Integer> count = new HashMap<>();
        for(Map.Entry<Long, List<CountFollowUnfollowMqDTO>> mapEntry: targetUserId2CountFollowUnfollowMqDTOs.entrySet()) {
            List<CountFollowUnfollowMqDTO> list = mapEntry.getValue();
            int finalCount = 0;
            for(CountFollowUnfollowMqDTO dto: list) {
                Integer type = dto.getType();
                FollowUnfollowTypeEnum followUnfollowTypeEnum = FollowUnfollowTypeEnum.valueOf(type);
                if(Objects.isNull(followUnfollowTypeEnum)) continue;
                switch (followUnfollowTypeEnum) {
                    case FOLLOW -> finalCount++;
                    case UNFOLLOW -> finalCount--;
                }
            }
            count.put(mapEntry.getKey(), finalCount);
        }

        // 更新redis
        log.info("==========> 聚合后计数：{}", count);
        count.forEach((K, V) -> {
            String redisKey = RedisKeyConstants.buildCountUserKey(K);
            boolean existed = redisTemplate.hasKey(redisKey);

            if(existed) {
                redisTemplate.opsForValue().set(redisKey, RedisKeyConstants.FIELD_FANS_TOTAL, V);
            }
        });

        // TODO: 发送更新数据库MQ

    }
}
