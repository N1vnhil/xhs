package org.n1vnhil.xhs.data.align.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.data.align.constants.MQConstants;
import org.n1vnhil.xhs.data.align.constants.RedisKeyConstants;
import org.n1vnhil.xhs.data.align.constants.TableConstants;
import org.n1vnhil.xhs.data.align.domain.mapper.InsertMapper;
import org.n1vnhil.xhs.data.align.model.dto.CollectUncollectNoteMqDTO;
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
    consumerGroup = "xhs_data_align_" + MQConstants.TOPIC_COUNT_NOTE_COLLECT,
    topic = MQConstants.TOPIC_COUNT_NOTE_COLLECT
)
@Component
@Slf4j
public class TodayNoteCollectUncollectData2DBConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private InsertMapper insertMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Value("${table.shards}")
    private int tableShards;

    @Override
    public void onMessage(String body) {
        log.info("## TodayNoteCollectIncrementData2DBConsumer 消费到了MQ: {}", body);
        CollectUncollectNoteMqDTO collectUncollectNoteMqDTO = JsonUtils.parseObject(body, CollectUncollectNoteMqDTO.class);
        if(Objects.isNull(collectUncollectNoteMqDTO)) return;

        Long noteId = collectUncollectNoteMqDTO.getNoteId();
        Long noteCreatorId = collectUncollectNoteMqDTO.getNoteCreatorId();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String noteIdsBloomKey = RedisKeyConstants.buildBloomUserNoteCollectNoteIdsListKey(date);
        String userIdsBloomKey = RedisKeyConstants.buildBloomUserNoteCollectUserIdsListKey(date);

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/bloom_today_note_collect_check.lua")));
        script.setResultType(Long.class);
        Long resultNote = redisTemplate.execute(script, Collections.singletonList(noteIdsBloomKey), noteId);
        Long resultUser = redisTemplate.execute(script, Collections.singletonList(userIdsBloomKey), noteCreatorId);
        RedisScript<Long> bloomAddScript = RedisScript.of("return redis.call('BF.ADD', KEYS[1], ARGV[1])", Long.class);

        if(Objects.equals(resultNote, 0L)) {
            try {
                insertMapper.insert2DataAlignNoteCollectCountTempTable(TableConstants
                        .buildTableNameSuffix(date, noteId % tableShards), noteId);
            } catch (Exception e) {
                log.error("", e);
            }

            redisTemplate.execute(bloomAddScript, Collections.singletonList(noteIdsBloomKey), noteId);
        }

        if(Objects.equals(resultUser, 0L)) {
            try {
                insertMapper.insert2DataAlignUserCollectCountTempTable(TableConstants
                        .buildTableNameSuffix(date, noteCreatorId % tableShards), noteCreatorId);
            } catch (Exception e) {
                log.error("", e);
            }

            redisTemplate.execute(bloomAddScript, Collections.singletonList(userIdsBloomKey), noteCreatorId);
        }
    }

}
