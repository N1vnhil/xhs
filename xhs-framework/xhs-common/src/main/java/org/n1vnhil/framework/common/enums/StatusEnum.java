package org.n1vnhil.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态枚举类
 * */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    ENABLE(0),
    DISABLE(1);

    public final Integer value;

}
