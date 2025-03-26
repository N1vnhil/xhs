package org.n1vnhil.xhs.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.n1vnhil.framework.common.exception.BaseExceptionInterface;


/**
 * 状态码枚举类
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("NOTE-10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("NOTE-10001", "参数错误"),

    // ----------- 业务异常状态码 -----------
    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}
