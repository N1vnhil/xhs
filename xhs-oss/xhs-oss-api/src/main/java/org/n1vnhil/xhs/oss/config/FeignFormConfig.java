package org.n1vnhil.xhs.oss.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignFormConfig {

    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

}
