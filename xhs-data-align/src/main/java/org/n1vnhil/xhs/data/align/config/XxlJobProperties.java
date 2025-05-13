package org.n1vnhil.xhs.data.align.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    public static final String PREFIX = "xxl.job";

    private String adminAddresses;

    private String accessToken;

    private String ip;

    private int port;

    private String logPath;

    private int logRetentionDays = 30;

    private String appName;

}
