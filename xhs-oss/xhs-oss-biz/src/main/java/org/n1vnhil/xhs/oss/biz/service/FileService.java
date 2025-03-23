package org.n1vnhil.xhs.oss.biz.service;

import org.n1vnhil.framework.common.response.Response;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件
     * @param file
     * @return
     */
    Response<?> uploadFile(MultipartFile file);

}
