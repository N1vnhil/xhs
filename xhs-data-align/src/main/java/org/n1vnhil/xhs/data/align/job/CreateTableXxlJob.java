package org.n1vnhil.xhs.data.align.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.n1vnhil.xhs.data.align.constants.TableConstants;
import org.n1vnhil.xhs.data.align.domain.mapper.CreateTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RefreshScope
public class CreateTableXxlJob {

    @Value("${table.shards}")
    private int tableShards;

    @Autowired
    private CreateTableMapper createTableMapper;

    @XxlJob("createTableJobHandler")
    public void createTableJobHandler() throws Exception {
        String date = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        XxlJobHelper.log("## 开始初始化明日增量数据表");

        if(tableShards > 0) {
            for(int i = 0; i < tableShards; i++) {
                String tableNameSuffix = TableConstants.buildTableNameSuffix(date, i);
                createTableMapper.createDataAlignFollowingCountTemplateTable(tableNameSuffix);
                createTableMapper.createDataAlignNoteCollectCountTemplateTable(tableNameSuffix);
                createTableMapper.createDataAlignNoteLikeCountTemplateTable(tableNameSuffix);
                createTableMapper.createDataAlignUserLikeCountTemplateTable(tableNameSuffix);
                createTableMapper.createDataAlignUserCollectCountTemplateTable(tableNameSuffix);
                createTableMapper.createDataAlignFansCountTemplateTable(tableNameSuffix);
                createTableMapper.createDataAlignNotePublishCountTemplateTable(tableNameSuffix);
            }
        }

        XxlJobHelper.log("## 结束初始化明日增量数据表，日期：{}", date);
    }

}
