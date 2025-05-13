package org.n1vnhil.xhs.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CollectUncollectNoteTypeEnum {

    COLLECT(1),
    UNCOLLECT(0)
    ;

    private final Integer code;

}
