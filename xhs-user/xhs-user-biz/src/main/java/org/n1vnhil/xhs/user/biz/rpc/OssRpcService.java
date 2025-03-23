package org.n1vnhil.xhs.user.biz.rpc;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.oss.api.FileFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class OssRpcService {

    @Autowired
    private FileFeignApi fileFeignApi;

    public String uploadFile(MultipartFile file) {
        Response<?> response = fileFeignApi.uploadFile(file);
        if(!response.isSuccess()) return null;
        return (String) response.getData();
    }

}
