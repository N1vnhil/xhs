package org.n1vnhil.xhs.user.relation.biz.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.xhs.user.relation.biz.constant.MQConstants;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "xhs_group", topic = MQConstants.TOPIC_FOLLOW_OR_UNFOLLOW)
public class FollowUnfollowConsumer implements RocketMQListener<Message> {

    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody());
        String tags = message.getTags();
        log.info("==========> 消费消息：{}, 标签{}", body, tags);

    }

}
