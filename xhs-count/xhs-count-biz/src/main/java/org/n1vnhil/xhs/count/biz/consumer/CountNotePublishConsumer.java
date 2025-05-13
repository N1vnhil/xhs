package org.n1vnhil.xhs.count.biz.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.count.biz.domain.mapper.UserCountDOMapper;
import org.n1vnhil.xhs.count.biz.model.dto.NoteOperateMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RocketMQMessageListener(
        consumerGroup = "xhs_group_" + MQConstants.TOPIC_NOTE_OPERATE,
        topic = MQConstants.TOPIC_NOTE_OPERATE
)
@Component
@Slf4j
public class CountNotePublishConsumer implements RocketMQListener<Message> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserCountDOMapper userCountDOMapper;

    @Override
    public void onMessage(Message message) {
        String bodyJsonStr = new String(message.getBody());
        String tags = message.getTags();
        log.info("==> 消费消息 {}，tags: {}", bodyJsonStr, tags);
        if(Objects.equals(tags, MQConstants.TAG_NOTE_PUBLISH)) {
            handleNoteTagMessage(bodyJsonStr, 1);
        } else if(Objects.equals(tags, MQConstants.TAG_NOTE_DELETE)) {
            handleNoteTagMessage(bodyJsonStr, -1);
        }
    }

    private void handleNoteTagMessage(String bodyStr, int count) {
        NoteOperateMqDTO noteOperateMqDTO = JsonUtils.parseObject(bodyStr, NoteOperateMqDTO.class);
        if(Objects.isNull(noteOperateMqDTO)) return;

        Long creatorId = noteOperateMqDTO.getCreatorId();
        String countUserRedisKey = RedisKeyConstants.buildCountUserKey(creatorId);
        boolean countUserExisted = redisTemplate.hasKey(countUserRedisKey);
        if(countUserExisted) {
            redisTemplate.opsForHash().increment(countUserRedisKey, RedisKeyConstants.FIELD_NOTE_TOTAL, count);
        }

        userCountDOMapper.insertOrUpdateNoteTotalByUserId(count, creatorId);
    }

}
