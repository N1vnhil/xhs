package org.n1vnhil.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.n1vnhil.framework.common.exception.BaseExceptionInterface;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ------------- 通用异常状态码 -------------
    SYSTEM_ERROR("500", "系统错误"),
    UNAUTHORIZED("401", "权限不足"),

    // ------------- 业务异常状态码 -------------
    ;

    private final String errorCode;

    private final String errorMessage;

}
