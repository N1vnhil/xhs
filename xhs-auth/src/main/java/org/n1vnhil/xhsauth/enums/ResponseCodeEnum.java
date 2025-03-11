package org.n1vnhil.xhsauth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.n1vnhil.framework.common.exception.BaseExceptionInterface;


@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {
    /** ================== 通用异常状态码 ================== **/
    SYSTEM_ERROR("AUTH-10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("AUTH-10001", "参数错误"),

    /** ================== 业务异常状态码 ================== **/
    VERIFICATION_CODE_SEND_FREQUENTLY("AUTH-20000", "请勿频繁申请验证码");

    private final String errorCode;

    private final String errorMessage;


}
