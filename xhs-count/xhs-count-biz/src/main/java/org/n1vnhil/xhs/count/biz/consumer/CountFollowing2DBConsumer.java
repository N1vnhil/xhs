package org.n1vnhil.xhs.count.biz.consumer;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.domain.mapper.UserCountDOMapper;
import org.n1vnhil.xhs.count.biz.enums.FollowUnfollowTypeEnum;
import org.n1vnhil.xhs.count.biz.model.dto.CountFollowUnfollowMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_FOLLOWING_2_DB, topic = MQConstants.TOPIC_COUNT_FOLLOWING_2_DB)
public class CountFollowing2DBConsumer implements RocketMQListener<String> {

    private RateLimiter rateLimiter = RateLimiter.create(5000);

    @Autowired
    private UserCountDOMapper userCountDOMapper;

    @Override
    public void onMessage(String body) {
        rateLimiter.acquire();
        log.info("==========> 消费MQ【计数服务：关注数入库】");

        if(StringUtils.isBlank(body)) return;
        CountFollowUnfollowMqDTO countFollowUnfollowMqDTO = JsonUtils.parseObject(body, CountFollowUnfollowMqDTO.class);
        Integer type = countFollowUnfollowMqDTO.getType();
        Long userId = countFollowUnfollowMqDTO.getUserId();
        int count = Objects.equals(type, FollowUnfollowTypeEnum.FOLLOW.getType()) ? 1 : -1;
        userCountDOMapper.insertOrUpdateFansTotalByUserId(count, userId);

    }
}
