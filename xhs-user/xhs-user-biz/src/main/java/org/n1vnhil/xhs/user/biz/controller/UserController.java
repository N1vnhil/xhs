package org.n1vnhil.xhs.user.biz.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.biz.model.vo.UpdateUserReqVO;
import org.n1vnhil.xhs.user.biz.service.UserService;
import org.n1vnhil.xhs.user.dto.req.FindUserByPhoneReqDTO;
import org.n1vnhil.xhs.user.dto.req.RegisterUserReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/update",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<?> update(@Validated UpdateUserReqVO updateUserReqVO) {
        return userService.updateUserInfo(updateUserReqVO);
    }

    @PostMapping("/register")
    @ApiOperationLog(description = "用户注册")
    public Response<?> register(@Validated @RequestBody RegisterUserReqDTO registerUserReqDTO) {
        return userService.register(registerUserReqDTO);
    }

    @PostMapping("/findByPhone")
    @ApiOperationLog(description = "根据手机号查询用户")
    public Response<?> getUserByPhone(@Validated @RequestBody FindUserByPhoneReqDTO findUserByPhoneReqDTO) {
        return userService.findUserByPhone(findUserByPhoneReqDTO);
    }
}
