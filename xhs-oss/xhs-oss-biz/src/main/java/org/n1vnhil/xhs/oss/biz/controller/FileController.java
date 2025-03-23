package org.n1vnhil.xhs.oss.biz.controller;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.oss.biz.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@Slf4j
@RequestMapping("/file")
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<?> uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

}
