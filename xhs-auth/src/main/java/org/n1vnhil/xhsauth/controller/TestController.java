package org.n1vnhil.xhsauth.controller;

import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.domain.dataobject.User;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PostMapping("/test2")
    @ApiOperationLog(description = "测试接口2")
    public Response<User> test2(@RequestBody User user) {
        int i = 1 / 0;
        return Response.success(user);
    }
}
