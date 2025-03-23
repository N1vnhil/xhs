package org.n1vnhil.xhs.oss.biz.strategy.impl;

import com.aliyun.oss.OSS;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.xhs.oss.biz.config.AliyunOssProperties;
import org.n1vnhil.xhs.oss.biz.strategy.FileStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;


@Slf4j
public class AliyunOSSFileStrategy implements FileStrategy {

    @Resource
    private AliyunOssProperties aliyunOssProperties;

    @Resource
    private OSS aliyunOssClient;

    @SneakyThrows
    @Override
    public String uploadFile(MultipartFile multipartFile, String bucketName) {
        log.info("==========> 上传文件至阿里云oss");

        if(multipartFile == null || multipartFile.getSize() == 0) {
            throw new RuntimeException("文件不能为空.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));

        String key = UUID.randomUUID().toString().replace("-", "");
        String objectName = String.format("%s%s", key, suffix);

        log.info("==========> 上传文件：{}", objectName);
        aliyunOssClient.putObject(bucketName, objectName, new ByteArrayInputStream(multipartFile.getInputStream().readAllBytes()));
        String url = String.format("https://%s.%s/%s", bucketName, aliyunOssProperties.getEndpoint(), objectName);
        log.info("==========> 文件上传至：{}", url);
        return url;
    }

}
