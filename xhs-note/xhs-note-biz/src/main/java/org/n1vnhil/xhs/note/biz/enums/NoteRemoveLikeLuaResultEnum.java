package org.n1vnhil.xhs.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoteRemoveLikeLuaResultEnum {

    NOT_EXIST(-1L),
    NOTE_LIKED(1L),
    NOTE_NOT_LIKED(-1L)
    ;

    private final Long code;

    public static NoteRemoveLikeLuaResultEnum valueOf(Long code) {
        for(NoteRemoveLikeLuaResultEnum noteRemoveLikeLuaResultEnum: NoteRemoveLikeLuaResultEnum.values()) {
            if(noteRemoveLikeLuaResultEnum.getCode().equals(code)) return noteRemoveLikeLuaResultEnum;
        }
        return null;
    }

}
