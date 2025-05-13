package org.n1vnhil.xhs.count.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CollectUncollectNoteTypeEnum {

    COLLECT(1),
    UNCOLLECT(0),
    ;

    private final Integer code;

    public static CollectUncollectNoteTypeEnum valueOf(Integer code) {
        for(CollectUncollectNoteTypeEnum collectUncollectNoteTypeEnum: CollectUncollectNoteTypeEnum.values()) {
            if(Objects.equals(collectUncollectNoteTypeEnum.getCode(), code)) {
                return collectUncollectNoteTypeEnum;
            }
        }
        return null;
    }

}
