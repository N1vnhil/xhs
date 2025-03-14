package org.n1vnhil.xhsauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.model.vo.user.UserLoginReqVO;
import org.n1vnhil.xhsauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperationLog(description = "用户登录")
    public Response<String> login(@Validated @RequestBody UserLoginReqVO userLoginReqVO) {
        return userService.loginAndRegister(userLoginReqVO);
    }

}
