package org.n1vnhil.xhs.note.biz.consumer;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.xhs.note.biz.constant.MQConstants;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_LIKE_OR_UNLIKE,
    topic = MQConstants.TOPIC_LIKE_OR_UNLIKE,
        consumeMode = ConsumeMode.ORDERLY
)
public class LikeUnlikeNoteConsumer implements RocketMQListener<Message> {

    private RateLimiter rateLimiter = RateLimiter.create(5000);

    @Override
    public void onMessage(Message message) {
        rateLimiter.acquire();

        String bodyJsonStr = new String(message.getBody());
        String tags = message.getTags();
        log.info("=========> 【LikeUnlikeNoteConsumer】消费消息: {}, tags: {}", bodyJsonStr, tags);

        if(Objects.equals(tags, MQConstants.TAG_LIKE)) handleLikeNoteTagMessage();
        else if(Objects.equals(tags, MQConstants.TAG_UNLIKE)) handleUnlikeNoteTageMessage();
    }

    /**
     * 处理笔记取消赞
     */
    private void handleUnlikeNoteTageMessage() {
    }

    /**
     * 处理笔记点赞
     */
    private void handleLikeNoteTagMessage() {
    }
}
