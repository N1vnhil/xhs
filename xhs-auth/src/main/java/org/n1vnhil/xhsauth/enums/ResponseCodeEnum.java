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
    VERIFICATION_CODE_SEND_FREQUENTLY("AUTH-20000", "请勿频繁申请验证码"),
    VERIFICATION_CODE_WRONG("AUTH-20001", "验证码错误"),
    LOGIN_TYPE_WRONG("AUTH-20002", "登录类型错误"),
    USER_NOT_FOUND("AUTH-20003", "该用户不存在"),
    PASSWORD_OR_PHONE_WRONG("AUTH-20004", "手机号或密码错误")
    ;

    private final String errorCode;

    private final String errorMessage;


}
