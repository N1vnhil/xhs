package org.n1vnhil.xhs.data.align.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.n1vnhil.xhs.data.align.constants.TableConstants;
import org.n1vnhil.xhs.data.align.domain.mapper.DeleteTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DeleteTableXxlJob {

    @Value("${table.shards}")
    private int tableShards;

    @Autowired
    private DeleteTableMapper deleteTableMapper;

    @XxlJob("deleteTableJobHandler")
    public void deleteTableJobHandler() throws Exception {
        XxlJobHelper.log("## 开始删除近一个月的日增量临时表");
        LocalDate today = LocalDate.now();
        LocalDate startDate = today;
        LocalDate endDate = today.minusMonths(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        while(startDate.isAfter(endDate)) {
            startDate.minusDays(1);
            String date = startDate.format(formatter);
            for(long i = 0; i < tableShards; i++) {
                String tableNameSuffix = TableConstants.buildTableNameSuffix(date, i);
                XxlJobHelper.log("删除表后缀: {}", tableNameSuffix);
                deleteTableMapper.deleteDataAlignFansCountTempTable(tableNameSuffix);
                deleteTableMapper.deleteDataAlignFollowingCountTempTable(tableNameSuffix);
                deleteTableMapper.deleteDataAlignNoteCollectCountTempTable(tableNameSuffix);
                deleteTableMapper.deleteDataAlignNoteLikeCountTempTable(tableNameSuffix);
                deleteTableMapper.deleteDataAlignNotePublishCountTempTable(tableNameSuffix);
                deleteTableMapper.deleteDataAlignUserCollectCountTempTable(tableNameSuffix);
                deleteTableMapper.deleteDataAlignUserLikeCountTempTable(tableNameSuffix);
            }
        }
        XxlJobHelper.log("## 完成近一个月增量临时表删除");
    }

}
