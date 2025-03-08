package org.n1vnhil.xhsauth.controller;

import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.n1vnhil.framework.common.response.Response;

import java.time.LocalDateTime;

@RestController
public class TestController {

    @ApiOperationLog(description = "测试接口1")
    @GetMapping("/test1")
    public Response<String> test1() {
        return Response.success("Test success.");
    }

    @ApiOperationLog(description = "测试接口2")
    @GetMapping("/test2")
    public Response<User> test2() {
        return Response.success(
                User.builder()
                        .nickname("n1vnhil")
                        .createTime(LocalDateTime.now())
                        .build()
        );
    }
}
