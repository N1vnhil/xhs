package org.n1vnhil.xhs.user.biz.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.biz.model.vo.UpdateUserReqVO;
import org.n1vnhil.xhs.user.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
