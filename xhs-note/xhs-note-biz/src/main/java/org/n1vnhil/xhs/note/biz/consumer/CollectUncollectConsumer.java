package org.n1vnhil.xhs.note.biz.consumer;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.note.biz.constant.MQConstants;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteCollectionDO;
import org.n1vnhil.xhs.note.biz.domain.mapper.NoteCollectionDOMapper;
import org.n1vnhil.xhs.note.biz.model.dto.CollectUncollectNoteMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COLLECT_OR_UNCOLLECT,
    topic = MQConstants.TAG_COLLECT,
    consumeMode = ConsumeMode.ORDERLY
)
@Slf4j
public class CollectUncollectConsumer implements RocketMQListener<Message> {

    private RateLimiter rateLimiter = RateLimiter.create(5000);

    @Autowired
    private NoteCollectionDOMapper noteCollectionDOMapper;

    @Override
    public void onMessage(Message message) {
        rateLimiter.acquire();
        String bodyJsonStr = new String(message.getBody());
        String tags = message.getTags();
        log.info("==> CollectUncollectConsumer 消费消息 {}，tags：{}", bodyJsonStr, tags);
        if(Objects.equals(tags, MQConstants.TAG_COLLECT)) {
            handleCollectMessage(bodyJsonStr);
        } else if (Objects.equals(tags, MQConstants.TAG_UNCOLLECT)) {
            handleUncollectMessage(bodyJsonStr);
        }
    }

    /**
     * 笔记收藏
     * @param bodyJsonStr
     */
    private void handleCollectMessage(String bodyJsonStr) {
        CollectUncollectNoteMqDTO collectUncollectNoteMqDTO = JsonUtils.parseObject(bodyJsonStr, CollectUncollectNoteMqDTO.class);
        if(Objects.isNull(collectUncollectNoteMqDTO)) return;

        NoteCollectionDO noteCollectionDO = NoteCollectionDO.builder()
                .userId(collectUncollectNoteMqDTO.getUserId())
                .noteId(collectUncollectNoteMqDTO.getNoteId())
                .createTime(collectUncollectNoteMqDTO.getCreateTime())
                .status(collectUncollectNoteMqDTO.getType())
                .build();

        int count = noteCollectionDOMapper.insertOrUpdate(noteCollectionDO);
    }

    /**
     * 笔记取消收藏
     * @param bodyJsonStr
     */
    private void handleUncollectMessage(String bodyJsonStr) {

    }
}
