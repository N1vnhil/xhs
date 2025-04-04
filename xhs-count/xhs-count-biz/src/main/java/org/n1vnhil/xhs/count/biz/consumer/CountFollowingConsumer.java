package org.n1vnhil.xhs.count.biz.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.count.biz.enums.FollowUnfollowTypeEnum;
import org.n1vnhil.xhs.count.biz.model.dto.CountFollowUnfollowMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_FOLLOWING,
        topic = MQConstants.TOPIC_COUNT_FOLLOWING)
public class CountFollowingConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(String body) {
        log.info("【MQ消费者】: 消费关注计数消息, body: {}", body);
        if(StringUtils.isBlank(body)) return;

        // 由于关注数的计数场景使得单个用户无法在短时间关注大部分用户，所以无需聚合
        CountFollowUnfollowMqDTO countFollowUnfollowMqDTO = JsonUtils.parseObject(body, CountFollowUnfollowMqDTO.class);

        Integer type = countFollowUnfollowMqDTO.getType();
        Long userId = countFollowUnfollowMqDTO.getUserId();
        String redisKey = RedisKeyConstants.buildCountUserKey(userId);
        boolean existed = redisTemplate.hasKey(redisKey);

        if(existed) {
            long count = Objects.equals(type, FollowUnfollowTypeEnum.FOLLOW.getType()) ? 1 : -1;
            redisTemplate.opsForHash().increment(redisKey, RedisKeyConstants.FIELD_FOLLOWING_TOTAL, count);
        }

        Message<String> message = MessageBuilder.withPayload(body).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_COUNT_FOLLOWING_2_DB, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("【消费服务：关注数入库】消费成功, {}", message);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("【消费服务：关注数入库】消费是被", throwable);
            }
        });
    }
}
