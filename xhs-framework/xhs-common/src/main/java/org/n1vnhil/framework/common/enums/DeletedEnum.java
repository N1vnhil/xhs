package org.n1vnhil.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 逻辑删除枚举类
 * */
@AllArgsConstructor
@Getter
public enum DeletedEnum {

    YES(true),
    NO(false);

    private final boolean deleted;
}
