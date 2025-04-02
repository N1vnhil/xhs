package org.n1vnhil.xhs.count.biz.consumer;

import com.github.phantomthief.collection.BufferTrigger;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_FANS,
        topic = MQConstants.TOPIC_COUNT_FANS)
public class CountFansConsumer implements RocketMQListener<String> {

    private BufferTrigger<String> bufferTrigger = BufferTrigger.<String>batchBlocking()
            .bufferSize(50000) // 缓存队列的最大容量
            .batchSize(1000)   // 一批次最多聚合 1000 条
            .linger(Duration.ofSeconds(1)) // 多久聚合一次
            .setConsumerEx(this::consumeMessage)
            .build();

    @Override
    public void onMessage(String body) {
        bufferTrigger.enqueue(body);
    }

    public void consumeMessage(List<String> bodys) {
        log.info("==========> 聚合消息，size: {}", bodys.size());
        log.info("==========> 聚合消息，{}", JsonUtils.toJsonString(bodys));
    }
}
