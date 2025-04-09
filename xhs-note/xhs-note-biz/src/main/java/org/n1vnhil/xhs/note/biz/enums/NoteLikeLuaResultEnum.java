package org.n1vnhil.xhs.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum NoteLikeLuaResultEnum {

    NOT_EXIST(-1L),
    NOTE_LIKED(1L),
    NOTE_LIKE_SUCCESS(0L)
    ;

    private final Long code;

    public static NoteLikeLuaResultEnum valueOf(Long code) {
        for(NoteLikeLuaResultEnum noteLikeLuaResultEnum: NoteLikeLuaResultEnum.values()) {
            if(Objects.equals(noteLikeLuaResultEnum.getCode(), code)) return noteLikeLuaResultEnum;
        }
        return null;
    }
}
