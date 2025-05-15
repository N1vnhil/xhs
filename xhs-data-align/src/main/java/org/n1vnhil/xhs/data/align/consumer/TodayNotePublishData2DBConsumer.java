package org.n1vnhil.xhs.data.align.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.data.align.constants.MQConstants;
import org.n1vnhil.xhs.data.align.constants.RedisKeyConstants;
import org.n1vnhil.xhs.data.align.constants.TableConstants;
import org.n1vnhil.xhs.data.align.domain.mapper.InsertMapper;
import org.n1vnhil.xhs.data.align.model.dto.NoteOperateMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Objects;

@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "xhs_group_date_align" + MQConstants.TOPIC_NOTE_OPERATE,
        topic = MQConstants.TOPIC_NOTE_OPERATE
)
public class TodayNotePublishData2DBConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private InsertMapper insertMapper;

    @Value("${table.shards}")
    private int tableShards;

    @Override
    public void onMessage(String body) {
        log.info("## TodayNotePublishIncrementData2DBConsumer 消费到了MQ: {}", body);
        NoteOperateMqDTO noteOperateMqDTO = JsonUtils.parseObject(body, NoteOperateMqDTO.class);

        if(Objects.isNull(noteOperateMqDTO)) return;

        Long noteCreatorId = noteOperateMqDTO.getCreatorId();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String bloomKey = RedisKeyConstants.buildBloomUserNoteOperateListKey(date);
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();

        script.setResultType(Long.class);
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/bloom_today_note_publish_check.lua")));
        Long result = redisTemplate.execute(script, Collections.singletonList(bloomKey), noteCreatorId);
        if(result.equals(0L)) {
            String tableSuffix = TableConstants.buildTableNameSuffix(date, noteCreatorId % tableShards);
            insertMapper.insert2DataAlignUserPublishCountTempTable(tableSuffix, noteCreatorId);
            RedisScript<Long> bloomAddScript = RedisScript.of("return redis.call('BF.ADD', KEYS[1], ARGV[1])", Long.class);
            redisTemplate.execute(bloomAddScript, Collections.singletonList(bloomKey), noteCreatorId);
        }
    }
}
