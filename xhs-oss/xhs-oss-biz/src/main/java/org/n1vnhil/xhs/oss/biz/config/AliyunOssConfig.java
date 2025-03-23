package org.n1vnhil.xhs.oss.biz.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunOssConfig {

    @Autowired
    private AliyuOssProperties aliyuOssProperties;

    @Bean
    public OSS aliyunOssClient() {
        DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(
                aliyuOssProperties.getAccessKeyId(), aliyuOssProperties.getAccessKeySecret());

        // 创建 OSSClient 实例
        return new OSSClientBuilder().build(aliyuOssProperties.getEndpoint(), credentialsProvider);
    }


}
