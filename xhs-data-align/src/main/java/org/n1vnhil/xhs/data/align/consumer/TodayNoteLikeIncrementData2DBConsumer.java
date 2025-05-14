package org.n1vnhil.xhs.data.align.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.data.align.constants.MQConstants;
import org.n1vnhil.xhs.data.align.constants.RedisKeyConstants;
import org.n1vnhil.xhs.data.align.constants.TableConstants;
import org.n1vnhil.xhs.data.align.domain.mapper.InsertRecordMapper;
import org.n1vnhil.xhs.data.align.model.dto.LikeUnlikeNoteMqDTO;
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

@RocketMQMessageListener(
        consumerGroup = "xhs_group_data_align_" + MQConstants.TOPIC_COUNT_NOTE_LIKE,
        topic = MQConstants.TOPIC_COUNT_NOTE_LIKE
)
@Component
@Slf4j
public class TodayNoteLikeIncrementData2DBConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private InsertRecordMapper insertRecordMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Value("${table.shards}")
    private int tableShards;

    @Override
    public void onMessage(String body) {
        log.info("## TodayNoteLikeIncrementData2DBConsumer 消费到 MQ: {}", body);
        LikeUnlikeNoteMqDTO likeUnlikeNoteMqDTO = JsonUtils.parseObject(body, LikeUnlikeNoteMqDTO.class);
        if(Objects.isNull(likeUnlikeNoteMqDTO)) return;

        Long noteId = likeUnlikeNoteMqDTO.getNoteId();;
        Long noteCreatorId = likeUnlikeNoteMqDTO.getNoteCreatorId();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String bloomKey = RedisKeyConstants.buildBloomUserNoteLikeListKey(date);

        // 布隆过滤器判空
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/bloom_today_note_like_check.lua")));
        script.setResultType(Long.class);
        Long result = redisTemplate.execute(script, Collections.singletonList(bloomKey), noteId);
        if(Objects.equals(result, 0L)) {
            // 数据落库
            Long userIdHashKey = noteCreatorId % tableShards;
            Long noteIdHashKey = noteId % tableShards;
            transactionTemplate.execute(status -> {
               try {
                   insertRecordMapper.insert2DataAlignNoteLikeCountTempTable(TableConstants
                           .buildTableNameSuffix(date, noteIdHashKey), noteIdHashKey);
                   insertRecordMapper.insert2DataAlignUserLikeCountTempTable(TableConstants
                           .buildTableNameSuffix(date, userIdHashKey), noteIdHashKey);
                   return true;
               } catch (Exception e) {
                   status.setRollbackOnly();
                   log.error("", e);
               }
               return false;
            });

            // 插入布隆过滤器
            RedisScript<Long> bloomAddScript = RedisScript.of("return redis.call('BF.ADD', KEYS[1], ARGV[1])", Long.class);
            redisTemplate.execute(bloomAddScript, Collections.singletonList(bloomKey), noteId);
        }

    }
}
