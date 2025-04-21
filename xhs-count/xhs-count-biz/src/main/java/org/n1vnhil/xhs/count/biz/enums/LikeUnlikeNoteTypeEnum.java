package org.n1vnhil.xhs.count.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeUnlikeNoteTypeEnum {

    LIKE(1),
    UNLIKE(0)
    ;

    private final Integer code;

    public static LikeUnlikeNoteTypeEnum valueOf(Integer code) {
        for(LikeUnlikeNoteTypeEnum likeUnlikeNoteTypeEnum: LikeUnlikeNoteTypeEnum.values()) {
            if(likeUnlikeNoteTypeEnum.getCode().equals(code)) return likeUnlikeNoteTypeEnum;
        }
        return null;
    }

}
