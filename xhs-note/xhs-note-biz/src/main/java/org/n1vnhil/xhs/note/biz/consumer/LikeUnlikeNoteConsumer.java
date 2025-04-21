package org.n1vnhil.xhs.note.biz.consumer;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.note.biz.constant.MQConstants;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteLikeDO;
import org.n1vnhil.xhs.note.biz.domain.mapper.NoteLikeDOMapper;
import org.n1vnhil.xhs.note.biz.model.dto.LikeUnlikeNoteMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_LIKE_OR_UNLIKE,
    topic = MQConstants.TOPIC_LIKE_OR_UNLIKE,
        consumeMode = ConsumeMode.ORDERLY
)
public class LikeUnlikeNoteConsumer implements RocketMQListener<Message> {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private NoteLikeDOMapper noteLikeDOMapper;

    private RateLimiter rateLimiter = RateLimiter.create(5000);

    @Override
    public void onMessage(Message message) {
        rateLimiter.acquire();

        String bodyJsonStr = new String(message.getBody());
        String tags = message.getTags();
        log.info("=========> 【LikeUnlikeNoteConsumer】消费消息: {}, tags: {}", bodyJsonStr, tags);

        if(Objects.equals(tags, MQConstants.TAG_LIKE)) handleLikeNoteTagMessage(bodyJsonStr);
        else if(Objects.equals(tags, MQConstants.TAG_UNLIKE)) handleUnlikeNoteTagMessage(bodyJsonStr);
    }

    /**
     * 处理笔记取消赞
     */
    private void handleUnlikeNoteTagMessage(String bodyJsonStr) {
        LikeUnlikeNoteMqDTO likeUnlikeNoteMqDTO = JsonUtils.parseObject(bodyJsonStr, LikeUnlikeNoteMqDTO.class);
        if(Objects.isNull(likeUnlikeNoteMqDTO)) return;

        Long userId = likeUnlikeNoteMqDTO.getUserId();
        Long noteId = likeUnlikeNoteMqDTO.getNoteId();
        Integer type = likeUnlikeNoteMqDTO.getType();
        LocalDateTime createTime = likeUnlikeNoteMqDTO.getCreateTime();

        NoteLikeDO noteLikeDO = NoteLikeDO.builder()
                .userId(userId)
                .noteId(noteId)
                .status(type)
                .createTime(createTime)
                .build();

        int count = noteLikeDOMapper.updateStatusByNoteIdAndUserId(noteLikeDO);
        if(count == 0) return;

        // 数据库更新成功，发送计数MQ
        org.springframework.messaging.Message<String> message = MessageBuilder.withPayload(bodyJsonStr).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_COUNT_NOTE_LIKE, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==> 【计数：笔记取消赞】MQ发送成功，sendReuslt: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==> 【计数：笔记取消赞】MQ发送异常", throwable);
            }
        });
    }

    /**
     * 处理笔记点赞
     */
    private void handleLikeNoteTagMessage(String bodyJsonStr) {
        LikeUnlikeNoteMqDTO likeUnlikeNoteMqDTO = JsonUtils.parseObject(bodyJsonStr, LikeUnlikeNoteMqDTO.class);
        if(Objects.isNull(likeUnlikeNoteMqDTO)) return;

        Long userId = likeUnlikeNoteMqDTO.getUserId();
        Long noteId = likeUnlikeNoteMqDTO.getNoteId();
        LocalDateTime createTime = likeUnlikeNoteMqDTO.getCreateTime();
        Integer type = likeUnlikeNoteMqDTO.getType();

        NoteLikeDO noteLikeDO = NoteLikeDO.builder()
                .noteId(noteId)
                .userId(userId)
                .createTime(createTime)
                .status(type)
                .build();

        int cnt = noteLikeDOMapper.insertOrUpadte(noteLikeDO);
        if(cnt == 0) return;

        // 数据库更新成功，发送计数MQ
        org.springframework.messaging.Message<String> message = MessageBuilder.withPayload(bodyJsonStr).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_COUNT_NOTE_LIKE, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==> 【计数：笔记点赞】MQ发送成功，sendReuslt: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==> 【计数：笔记点赞】MQ发送异常", throwable);
            }
        });
    }
}
