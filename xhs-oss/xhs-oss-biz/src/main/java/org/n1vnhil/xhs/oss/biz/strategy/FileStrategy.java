package org.n1vnhil.xhs.oss.biz.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件策略接口
 */
public interface FileStrategy {

    /**
     * @param multipartFile
     * @param bucketName
     * @return
     */
    String uploadFile(MultipartFile multipartFile, String bucketName);

}
