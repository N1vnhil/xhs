package org.n1vnhil.xhsauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.filter.LoginUserContextFilter;
import org.n1vnhil.xhsauth.model.vo.user.UpdatePasswordReqVO;
import org.n1vnhil.xhsauth.model.vo.user.UserLoginReqVO;
import org.n1vnhil.xhsauth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @ApiOperationLog(description = "用户登录")
    public Response<String> login(@Validated @RequestBody UserLoginReqVO userLoginReqVO) {
        return authService.loginAndRegister(userLoginReqVO);
    }

    @PostMapping("/logout")
    @ApiOperationLog(description = "用户登出")
    public Response<?> logout() {
        Long userId = LoginUserContextFilter.getLoginUserId();
        log.info("===========> 用户登出：{}", userId);
        return authService.logout(userId);
    }

    @PostMapping("/editPassword")
    @ApiOperationLog(description = "修改密码")
    public Response<?> editPassword(@Validated @RequestBody UpdatePasswordReqVO updatePasswordReqVO) {
        log.info("==========> 修改密码：{}", updatePasswordReqVO);
        return authService.updatePassword(updatePasswordReqVO);
    }
}
