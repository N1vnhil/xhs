package org.n1vnhil.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态枚举类
 * */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    ENABLE(1),
    DISABLE(0);

    public final Integer value;

}
