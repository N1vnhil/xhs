package org.n1vnhil.xhs.oss.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.n1vnhil.framework.common.exception.BaseExceptionInterface;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    /** ================== 通用异常状态码 ================== **/
    SYSTEM_ERROR("OSS-10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("OSS-10001", "参数错误"),

    /** ================== 业务异常状态码 ================== **/
    ;

    private final String errorCode;

    private final String errorMessage;

}
