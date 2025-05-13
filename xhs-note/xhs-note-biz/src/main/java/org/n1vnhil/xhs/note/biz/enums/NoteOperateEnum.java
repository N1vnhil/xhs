package org.n1vnhil.xhs.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoteOperateEnum {

    PUBLISH(1),
    DELETE(0),
    ;

    private final Integer code;

}
