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

    @Override
    public Response<?> uploadFile(MultipartFile file) {
        fileStrategy.uploadFile(file, "xhs");
        return Response.success();
    }

}
