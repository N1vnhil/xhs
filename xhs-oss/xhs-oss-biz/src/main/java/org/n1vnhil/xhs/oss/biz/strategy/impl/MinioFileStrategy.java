package org.n1vnhil.xhs.oss.biz.strategy.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.xhs.oss.biz.config.MinioProperties;
import org.n1vnhil.xhs.oss.biz.strategy.FileStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
public class MinioFileStrategy implements FileStrategy {

    @Resource
    private MinioProperties minioProperties;

    @Resource
    private MinioClient minioClient;

    @Override
    @SneakyThrows
    public String uploadFile(MultipartFile multipartFile, String bucketName) {
        log.info("==========> 上传文件至Minio");
        if(multipartFile == null || multipartFile.getSize() == 0) {
            throw new RuntimeException("文件不能为空.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();

        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String key = UUID.randomUUID().toString().replace("-", "");
        String objectName = String.format("%s%s", key, suffix);

        log.info("==========> 上传文件：{}", objectName);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .contentType(contentType)
                        .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                        .build()
        );

        String url = String.format("%s%s%s", minioProperties.getEndpoint(), bucketName, objectName);
        return url;
    }

}
