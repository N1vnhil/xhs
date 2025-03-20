package org.n1vnhil.exception;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.SaTokenContextException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.enums.ResponseCodeEnum;
import org.n1vnhil.framework.common.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.security.auth.login.LoginException;

@Slf4j
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        log.info("=========> 网关全局异常捕获：{}", ex.getMessage());

        Response<?> result;
        if(ex instanceof LoginException) {
            // 未登录
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            result = Response.fail(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(), ex.getMessage());
        } else if(ex instanceof NotPermissionException) {
            // 无权限
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            result = Response.fail(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(),
                    ResponseCodeEnum.UNAUTHORIZED.getErrorMessage());
        } else {
            // 其他错误
            result = Response.fail(ResponseCodeEnum.SYSTEM_ERROR);
        }

        // 设置请求格式
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                // 将result对象转换为字节数组
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
            } catch (Exception e) {
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }

}
