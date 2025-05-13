package org.n1vnhil.xhs.count.biz.consumer;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.n1vnhil.xhs.count.biz.domain.mapper.NoteCountDOMapper;
import org.n1vnhil.xhs.count.biz.domain.mapper.UserCountDOMapper;
import org.n1vnhil.xhs.count.biz.model.dto.AggregationCountCollectUncollectNoteMqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_NOTE_COLLECT_2_DB,
        topic = MQConstants.TOPIC_COUNT_NOTE_COLLECT_2_DB
)
public class CountNoteCollect2DBConsumer implements RocketMQListener<String> {

    private RateLimiter rateLimiter = RateLimiter.create(5000);

    @Autowired
    private NoteCountDOMapper noteCountDOMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserCountDOMapper userCountDOMapper;

    @Override
    public void onMessage(String body) {
        rateLimiter.acquire();
        log.info("## 消费 MQ 【计数：笔记收藏落库】, {}", body);
        List<AggregationCountCollectUncollectNoteMqDTO> countList = null;
        try {
            countList = JsonUtils.parseList(body, AggregationCountCollectUncollectNoteMqDTO.class);
        } catch (Exception e) {
            log.error("## JSON 解析异常：", e);
        }

        if(CollUtil.isNotEmpty(countList)) {
            countList.forEach(o -> {
                Long userId = o.getCreatorId();
                Long noteId = o.getNoteId();
                Integer count = o.getCount();

                transactionTemplate.execute(status -> {
                   try {
                       noteCountDOMapper.insertOrUpdateCollectTotalById(count, noteId);
                       userCountDOMapper.insertOrUpdateCollectTotalByUserId(count, userId);
                       return true;
                   } catch (Exception e) {
                       status.setRollbackOnly();
                       log.error("", e);
                   }
                   return false;
                });

            });
        }
    }
}
