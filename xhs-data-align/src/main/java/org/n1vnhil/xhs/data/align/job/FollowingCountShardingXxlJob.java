package org.n1vnhil.xhs.data.align.job;

import cn.hutool.core.collection.CollUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.xhs.data.align.constants.RedisKeyConstants;
import org.n1vnhil.xhs.data.align.constants.TableConstants;
import org.n1vnhil.xhs.data.align.domain.mapper.DeleteMapper;
import org.n1vnhil.xhs.data.align.domain.mapper.SelectMapper;
import org.n1vnhil.xhs.data.align.domain.mapper.UpdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class FollowingCountShardingXxlJob {

    @Autowired
    private SelectMapper selectMapper;

    @Autowired
    private UpdateMapper updateMapper;

    @Autowired
    private DeleteMapper deleteMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @XxlJob("followingCountShardingJobHandler")
    public void followingCountShardingJobHandler() throws Exception {
        long shardIndex = XxlJobHelper.getShardIndex();
        long shardTotal = XxlJobHelper.getShardTotal();
        XxlJobHelper.log("分片参数：当前分片：{}，总分片：{}", shardIndex, shardTotal);
        log.info("分片参数：当前分片：{}，总分片：{}", shardIndex, shardTotal);

        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tableNameSuffix = TableConstants.buildTableNameSuffix(date, shardIndex);
        int batchSize = 1000;
        int processTotal = 0;

        for(;;) {
            List<Long> userIds = selectMapper.selectBatchFromDataAlignFollowingCountTempTable(tableNameSuffix, batchSize);
            if(CollUtil.isEmpty(userIds)) break;
            userIds.forEach(userId -> {
                int followingTotal = selectMapper.selectCountFromFollowingTableByUserId(userId);
                int count = updateMapper.updateUserFollowingTotalByUser(userId, followingTotal);
                if(count > 0) {
                    String redisKey = RedisKeyConstants.buildCountUserKey(userId);
                    boolean hasKey = redisTemplate.hasKey(redisKey);
                    if(hasKey) redisTemplate.opsForHash().put(redisKey, RedisKeyConstants.FIELD_FOLLOWING_TOTAL, followingTotal);
                }
            });

            processTotal += userIds.size();
            deleteMapper.batchDeleteDataAlignFollowingCountTempTable(tableNameSuffix, userIds);

        }

        XxlJobHelper.log("==================> 结束定时分片广播任务：关注数对齐，共对齐记录数：{}", processTotal);
    }

}
