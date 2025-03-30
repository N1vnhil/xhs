package org.n1vnhil.xhs.user.relation.biz.consumer;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.DateUtils;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.user.relation.biz.constant.MQConstants;
import org.n1vnhil.xhs.user.relation.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.user.relation.biz.domain.dataobject.FanDO;
import org.n1vnhil.xhs.user.relation.biz.domain.dataobject.FollowingDO;
import org.n1vnhil.xhs.user.relation.biz.domain.mapper.FanDOMapper;
import org.n1vnhil.xhs.user.relation.biz.domain.mapper.FollowingDOMapper;
import org.n1vnhil.xhs.user.relation.biz.model.dto.FollowUserMqDTO;
import org.n1vnhil.xhs.user.relation.biz.model.dto.UnfollowUserMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "xhs_group",
        topic = MQConstants.TOPIC_FOLLOW_OR_UNFOLLOW,
        consumeMode = ConsumeMode.ORDERLY
)
public class FollowUnfollowConsumer implements RocketMQListener<Message> {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private FanDOMapper fanDOMapper;

    @Autowired
    private FollowingDOMapper followingDOMapper;

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public FollowUnfollowConsumer(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void onMessage(Message message) {
        // Guava令牌桶流量控制
        rateLimiter.acquire();

        String body = new String(message.getBody());
        String tags = message.getTags();
        log.info("==========> 消费消息：{}, 标签{}", body, tags);

        if(Objects.equals(tags, MQConstants.TAG_FOLLOW)) {
            handleFollowTagMessage(body);
        } else if(Objects.equals(tags, MQConstants.TAG_UNFOLLOW)) {
            handleUnfollowTagMessage(body);
        }
    }


    /**
     * 关注消息处理
     * @param body
     */
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
                                    .fansUserId(userId)
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

        // 更新 redis
        if(success) {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_check_and_update_fans_zset.lua")));
            script.setResultType(Long.class);

            String redisKey = RedisKeyConstants.buildUserFansKey(userId);
            long timestamp = DateUtils.localDateTime2Timestamp(createTime);
            redisTemplate.execute(script, Collections.singletonList(redisKey), userId, timestamp);
        }

    }

    /**
     * 取关消息处理
     * @param body
     */
    private void handleUnfollowTagMessage(String body) {
        UnfollowUserMqDTO unfollowUserMqDTO = JsonUtils.parseObject(body, UnfollowUserMqDTO.class);
        if(Objects.isNull(unfollowUserMqDTO)) return;
        Long userId = unfollowUserMqDTO.getUserId();
        Long unfollowUserId = unfollowUserMqDTO.getUnfollowUserId();
        LocalDateTime createTime = unfollowUserMqDTO.getCreateTime();

        // 编程式提交事务
        boolean success = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            try {
                int cnt = followingDOMapper.deleteByUserIdAndUnfollowUserId(userId, unfollowUserId);
                if(cnt > 0) {
                    fanDOMapper.deleteByUserIdAndFansUserId(unfollowUserId, userId);
                }
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("", e);
            }
            return false;
        }));

        // 事务提交成功，从redis缓存中删除粉丝数据
        if(success) {
            String fansRedisKey = RedisKeyConstants.buildUserFansKey(userId);
            redisTemplate.opsForZSet().remove(fansRedisKey, userId);
        }
    }

}
