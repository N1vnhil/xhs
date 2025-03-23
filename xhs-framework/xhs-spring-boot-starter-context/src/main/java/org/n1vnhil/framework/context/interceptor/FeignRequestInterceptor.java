package org.n1vnhil.framework.context.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.constant.GlobalConstants;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;

import java.util.Objects;

@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId = LoginUserContextHolder.getLoginUserId();
        if(Objects.nonNull(userId)) {
            requestTemplate.header(GlobalConstants.USER_ID, String.valueOf(userId));
            log.info("Feign设置请求头，userId：{}", userId);
        }
    }
}
