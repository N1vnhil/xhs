package org.n1vnhil.xhs.oss.biz.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.xhs.oss.biz.strategy.FileStrategy;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class MinioFileStrategy implements FileStrategy {

    public String uploadFile(MultipartFile multipartFile, String bucketName) {
        log.info("==========> 上传文件至Minio");
        return null;
    }

}
