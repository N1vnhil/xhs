package org.n1vnhil.xhs.oss.biz.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunOssConfig {

    @Autowired
    private AliyunOssProperties aliyunOssProperties;

    @Bean
    public OSS aliyunOssClient() {
        DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(
                aliyunOssProperties.getAccessKey(), aliyunOssProperties.getSecretKey());
        return new OSSClientBuilder().build(aliyunOssProperties.getEndpoint(), credentialsProvider);
    }


}
