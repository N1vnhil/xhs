package org.n1vnhil.xhsauth.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.domain.dataobject.User;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @PostMapping("/test2")
    @ApiOperationLog(description = "测试接口2")
    public Response<User> test2(@RequestBody @Validated User user) {
//        int i = 1 / 0;
        return Response.success(user);
    }

    @RequestMapping("/user/doLogin")
    public String doLogin(String username, String password) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        if("zhang".equals(username) && "123456".equals(password)) {
            StpUtil.login(10001);
            return "登录成功";
        }
        return "登录失败";
    }

    @GetMapping("/user/isLogin")
    public String isLogin() {
        return "当前会话登录状态：" + StpUtil.isLogin();
    }
}
