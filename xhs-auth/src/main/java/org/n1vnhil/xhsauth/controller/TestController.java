package org.n1vnhil.xhsauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.n1vnhil.framework.common.response.Response;


@RestController
public class TestController {

    @GetMapping("/test")
    public Response<String> test() {
        return Response.success("Test success.");
    }

}
