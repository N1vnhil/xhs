package org.n1vnhil.xhs.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoteUncollectLuaResultEnum {

    NOT_EXIST(-1L),
    NOTE_COCLLECTED(1L),
    NOTE_NOT_COLLECTED(0L)
    ;

    private final Long code;

    public static NoteUncollectLuaResultEnum valueOf(Long code) {
        for(NoteUncollectLuaResultEnum noteUncollectLuaResultEnum: NoteUncollectLuaResultEnum.values()) {
            if(noteUncollectLuaResultEnum.getCode().equals(code)) {
                return noteUncollectLuaResultEnum;
            }
        }
        return null;
    }

}

