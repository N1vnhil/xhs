package org.n1vnhil.xhs.data.align.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.data.align.constants.MQConstants;
import org.n1vnhil.xhs.data.align.constants.RedisKeyConstants;
import org.n1vnhil.xhs.data.align.constants.TableConstants;
import org.n1vnhil.xhs.data.align.domain.mapper.InsertMapper;
import org.n1vnhil.xhs.data.align.model.dto.FollowUnfollowMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(
        consumerGroup = "xhs_group_data_align_" + MQConstants.TOPIC_COUNT_FOLLOW,
        topic = MQConstants.TOPIC_COUNT_FOLLOW
)
public class TodayNoteFollowingData2DBConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private InsertMapper insertMapper;

    @Value("${table.shards}")
    private int tableShards;

    @Override
    public void onMessage(String body) {
        log.info("## TodayNoteFollowingData2DBConsumer 消费到了 MQ, {}", body);
        FollowUnfollowMqDTO followUnfollowMqDTO = JsonUtils.parseObject(body, FollowUnfollowMqDTO.class);
        if(Objects.isNull(followUnfollowMqDTO)) return;

        Long userId = followUnfollowMqDTO.getUserId();
        Long targetId = followUnfollowMqDTO.getTargetUserId();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String userBloomKey = RedisKeyConstants.buildBloomUserFollowFollowingListKey(date);
        String targetUserBloomKey = RedisKeyConstants.buildBloomUserFollowFansListKey(date);
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/bloom_today_user_follow_check.lua")));
        script.setResultType(Long.class);
        RedisScript<Long> bloomAdd = RedisScript.of("return redis.call('BF.ADD', KEYS[1], ARGV[1])", Long.class);

        Long resultUser = redisTemplate.execute(script, Collections.singletonList(userBloomKey), userId);
        Long resultTargetUser = redisTemplate.execute(script, Collections.singletonList(targetUserBloomKey), targetId);

        if(resultUser.equals(0L)) {
            try {
                insertMapper.insert2DataAlignUserFollowingCountTempTable(TableConstants
                        .buildTableNameSuffix(date, userId % tableShards), userId);
            } catch (Exception e) {
                log.error("", e);
            }

            redisTemplate.execute(bloomAdd, Collections.singletonList(userBloomKey), userId);
        }

        if(resultTargetUser.equals(0L)) {
            try {
                insertMapper.insert2DataAlignUserFansCountTempTable(TableConstants
                        .buildTableNameSuffix(date, targetId % tableShards), targetId);
            } catch (Exception e) {
                log.error("", e);
            }
        }

        redisTemplate.execute(bloomAdd, Collections.singletonList(targetUserBloomKey), targetId);
    }
}
