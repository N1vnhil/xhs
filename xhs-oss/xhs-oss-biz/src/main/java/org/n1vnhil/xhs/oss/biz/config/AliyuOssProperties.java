package org.n1vnhil.xhs.oss.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "storage.aliyun-oss")
public class AliyuOssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
}
