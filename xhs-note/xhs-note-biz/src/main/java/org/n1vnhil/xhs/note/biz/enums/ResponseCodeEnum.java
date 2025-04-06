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
    NOTE_TYPE_ERROR("NOTE-20000", "未知的笔记类型"),
    NOTE_PUBLISH_FAIL("NOTE-20001", "笔记发布失败"),
    NOTE_NOT_FOUND("NOTE-20002", "笔记不存在"),
    NOTE_PRIVATE("NOTE-20003", "作者已将该笔记设置为仅自己可见"),
    NOTE_UPDATE_FAIL("NOTE-20004", "笔记更新失败"),
    NOTE_TOPIC_NOT_FOUND("NOTE-20005", "主题不存在"),
    NOTE_CANNOT_ONLY_ME("NOTE-20006", "笔记无法设置为仅自己可见"),
    NOTE_CANT_OPERATE("NOTE-20007", "您无法操作该笔记"),
    NOTE_ALREADY_LIKED("NOTE-20008", "您已经点赞过该笔记"),
    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}
