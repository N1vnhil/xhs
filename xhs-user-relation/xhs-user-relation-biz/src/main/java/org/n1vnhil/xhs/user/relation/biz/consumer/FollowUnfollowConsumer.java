package org.n1vnhil.xhs.user.relation.biz.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.user.relation.biz.constant.MQConstants;
import org.n1vnhil.xhs.user.relation.biz.domain.dataobject.FanDO;
import org.n1vnhil.xhs.user.relation.biz.domain.dataobject.FollowingDO;
import org.n1vnhil.xhs.user.relation.biz.domain.mapper.FanDOMapper;
import org.n1vnhil.xhs.user.relation.biz.domain.mapper.FollowingDOMapper;
import org.n1vnhil.xhs.user.relation.biz.model.dto.FollowUserMqDTO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FollowUserReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "xhs_group", topic = MQConstants.TOPIC_FOLLOW_OR_UNFOLLOW)
public class FollowUnfollowConsumer implements RocketMQListener<Message> {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private FanDOMapper fanDOMapper;

    @Autowired
    private FollowingDOMapper followingDOMapper;

    public FollowUnfollowConsumer(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody());
        String tags = message.getTags();
        log.info("==========> 消费消息：{}, 标签{}", body, tags);

        if(Objects.equals(tags, MQConstants.TAG_FOLLOW)) {
            handleFollowTagMessage(body);
        } else if(Objects.equals(tags, MQConstants.TAG_UNFOLLOW)) {
            handleUnfollowTagMessage(body);
        }
    }

    private void handleFollowTagMessage(String body) {
        FollowUserMqDTO followUserMqDTO = JsonUtils.parseObject(body, FollowUserMqDTO.class);
        if(Objects.isNull(followUserMqDTO)) return;
        Long userId = followUserMqDTO.getUserId();
        Long followId = followUserMqDTO.getFollowId();
        LocalDateTime createTime = followUserMqDTO.getCreateTime();

        boolean success = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            try {
                // 插入关注数据到关注表
                int cnt = followingDOMapper.insert(FollowingDO.builder()
                                .createTime(createTime)
                                .userId(userId)
                                .followingUserId(followId)
                        .build());

                // 若插入成功，插入关注数据到粉丝表
                if(cnt > 0) {
                    fanDOMapper.insert(FanDO.builder()
                                    .fanUserId(userId)
                                    .userId(followId)
                                    .createTime(createTime)
                            .build());
                }
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("");
            }
            return false;
        }));
    }

    private void handleUnfollowTagMessage(String body) {

    }

}
