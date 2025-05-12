package org.n1vnhil.xhs.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoteCollectLuaResultEnum {

    NOTE_NOT_EXIST(-1L),
    NOTE_COLLECTED(1L),
    NOTE_COLLECT_SUCCESS(0L)
    ;

    private final Long code;

    public static NoteCollectLuaResultEnum valueOf(Long code) {
        for(NoteCollectLuaResultEnum noteCollectLuaResultEnum: NoteCollectLuaResultEnum.values()) {
            if(noteCollectLuaResultEnum.getCode().equals(code)) return noteCollectLuaResultEnum;
        }
        return null;
    }

}
