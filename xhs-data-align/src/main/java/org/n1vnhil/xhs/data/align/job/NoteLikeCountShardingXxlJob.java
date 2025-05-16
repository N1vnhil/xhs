package org.n1vnhil.xhs.data.align.job;

import cn.hutool.core.collection.CollUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleState;
import org.n1vnhil.xhs.data.align.constants.RedisKeyConstants;
import org.n1vnhil.xhs.data.align.constants.TableConstants;
import org.n1vnhil.xhs.data.align.domain.mapper.DeleteMapper;
import org.n1vnhil.xhs.data.align.domain.mapper.SelectMapper;
import org.n1vnhil.xhs.data.align.domain.mapper.UpdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class NoteLikeCountShardingXxlJob {

    @Autowired
    private SelectMapper selectMapper;

    @Autowired
    private UpdateMapper updateMapper;

    @Autowired
    private DeleteMapper deleteMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @XxlJob("noteLikeCountShardingJobHandler")
    public void noteLikeCountShardingJobHandler() throws Exception {
        long shardIndex = XxlJobHelper.getShardIndex();
        long shardTotal = XxlJobHelper.getShardTotal();
        XxlJobHelper.log("======> 开始定时分片广播任务：对齐当日变更笔记点赞数");
        XxlJobHelper.log("分片参数：分片序号 {}，总分片数 {}", shardIndex, shardTotal);
        log.info("分片参数：分片序号 {}，总分片数 {}", shardIndex, shardTotal);
        String date = LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tableNameSuffix = TableConstants.buildTableNameSuffix(date, shardIndex);
        int batchSize = 1000;
        int processedTotal = 0;

        for(;;) {
            List<Long> noteIds = selectMapper.selectBatchFromDataAlignNoteLikeCountTempTable(tableNameSuffix, batchSize);
            if(CollUtil.isEmpty(noteIds)) break;
            noteIds.forEach(noteId -> {
                int likeTotal = selectMapper.selectCountFromLikeTableByNoteId(noteId);
                int count = updateMapper.updateNoteLikeTotalByNoteId(noteId, likeTotal);
                if (count > 0) {
                    String redisKey = RedisKeyConstants.buildCountNoteKey(noteId);
                    boolean hasKey = redisTemplate.hasKey(redisKey);
                    if (hasKey) {
                        redisTemplate.opsForHash().put(redisKey, RedisKeyConstants.FIELD_LIKE_TOTAL, likeTotal);
                    }
                }

            });
            deleteMapper.batchDeleteDataAlignNoteLikeCountTempTable(tableNameSuffix, noteIds);
            processedTotal += noteIds.size();
        }

        XxlJobHelper.log("==========> 结束定时任务分片广播：笔记点赞对齐，共对齐记录数：{}", processedTotal);
    }

}
