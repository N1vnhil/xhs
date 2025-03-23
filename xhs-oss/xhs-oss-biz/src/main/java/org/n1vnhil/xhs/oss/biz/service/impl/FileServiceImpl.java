package org.n1vnhil.xhs.oss.biz.service.impl;

import jakarta.annotation.Resource;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.oss.biz.service.FileService;
import org.n1vnhil.xhs.oss.biz.strategy.FileStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileStrategy fileStrategy;

    private static final String BUCKET_NAME = "xhs";

    @Override
    public Response<?> uploadFile(MultipartFile file) {
        String url = fileStrategy.uploadFile(file, BUCKET_NAME);
        return Response.success(url);
    }

}
