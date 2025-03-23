package org.n1vnhil.xhs.user.biz.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.response.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @PostMapping("/update")
    public Response<?> update() {
        return null;
    }

}
