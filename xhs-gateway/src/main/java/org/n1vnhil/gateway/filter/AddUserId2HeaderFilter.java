package org.n1vnhil.gateway.filter;


import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.constant.GlobalConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AddUserId2HeaderFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("===========> TokenConvertFilter");
        Long userId = null;

        try {
            userId = StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return chain.filter(exchange);
        }

        log.info("============> 当前用户ID：{}", userId);
        Long finalUserId = userId;
        ServerWebExchange newExchange = exchange.mutate()
                .request(builder -> builder.header(GlobalConstants.USER_ID, finalUserId.toString()))
                .build();

        return chain.filter(exchange);
    }

}
