package org.n1vnhil.xhs.oss.biz.factory;

import org.apache.commons.lang3.StringUtils;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.xhs.oss.biz.strategy.FileStrategy;
import org.n1vnhil.xhs.oss.biz.strategy.impl.AliyunOSSFileStrategy;
import org.n1vnhil.xhs.oss.biz.strategy.impl.MinioFileStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FileStrategyFactory {

    @Value("${storage.type}")
    private String strategyType;

    @Bean
    public FileStrategy fileStrategy() {
        if(StringUtils.equals(strategyType, "aliyun")) return new AliyunOSSFileStrategy();
        else if(StringUtils.equals(strategyType, "minio")) return new MinioFileStrategy();
        throw new IllegalArgumentException("不可用的存储类型");
    }

}
