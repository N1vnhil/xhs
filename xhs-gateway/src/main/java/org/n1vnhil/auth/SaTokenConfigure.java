package org.n1vnhil.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaTokenConfigure {

    /**
     * 注册SaToken全局过滤器
     * @return
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验
                    SaRouter.match("/**") // 拦截所有路由
                            .notMatch("/auth/user/login") // 排除登录接口
                            .notMatch("/auth/verification/sendCode") // 排除验证码发送接口
                            .check(r -> StpUtil.checkLogin()) // 校验是否登录
                    ;

                    SaRouter.match("/auth/user/logout", r -> StpUtil.checkRole("common_user"));
                })

                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    // 未登录异常
                    if(e instanceof NotLoginException) {
                        throw new NotLoginException(e.getMessage(), null, null);
                    }

                    // 权限不足异常 / 不具备对应角色异常
                    else if(e instanceof NotPermissionException || e instanceof NotRoleException) {
                        throw new NotPermissionException(e.getMessage());
                    }

                    else {
                        throw new RuntimeException(e.getMessage());
                    }
                });

    }

}
