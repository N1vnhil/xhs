package org.n1vnhil.xhs.count.biz.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_FOLLOWING,
        topic = MQConstants.TOPIC_COUNT_FOLLOWING)
public class CountFollowingConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String body) {
        log.info("【MQ消费者】: 消费关注计数消息, body: {}", body);
    }
}
