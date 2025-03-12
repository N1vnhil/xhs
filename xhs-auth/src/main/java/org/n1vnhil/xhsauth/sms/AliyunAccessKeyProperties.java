package org.n1vnhil.xhsauth.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "aliyun")
@Component
public class AliyunAccessKeyProperties {

    private String accessKeyId;

    private String accessKeySecret;

}
