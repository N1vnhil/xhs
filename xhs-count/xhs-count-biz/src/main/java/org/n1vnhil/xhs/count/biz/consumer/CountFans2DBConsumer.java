package org.n1vnhil.xhs.count.biz.consumer;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.count.biz.constant.MQConstants;
import cn.hutool.core.collection.CollUtil;
import org.n1vnhil.xhs.count.biz.domain.mapper.UserCountDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "xhs_group_" + MQConstants.TOPIC_COUNT_FANS_2_DB,
        topic = MQConstants.TOPIC_COUNT_FANS_2_DB)
public class CountFans2DBConsumer implements RocketMQListener<String> {

    @Autowired
    private UserCountDOMapper userCountDOMapper;

    private RateLimiter rateLimiter = RateLimiter.create(5000);

    @Override
    public void onMessage(String body) {
        rateLimiter.acquire();
        log.info("消费MQ【计数服务：粉丝数入库】，{}", body);

        Map<Long, Integer> map = null;
        try {
            map = JsonUtils.parseMap(body, Long.class, Integer.class);
        } catch (Exception e) {
            log.error("==========> 字符串解析错误：{}", body);
        }

        if(CollUtil.isNotEmpty(map)) {
            map.forEach((k, v) -> userCountDOMapper.insertOrUpdateFansTotalByUserId(v, k));
        }

    }
}
