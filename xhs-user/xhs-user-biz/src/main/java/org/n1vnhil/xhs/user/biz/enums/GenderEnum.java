package org.n1vnhil.xhs.user.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderEnum {

    WOMAN(0),
    MAN(1);

    private final Integer value;

    public static boolean valid(Integer value) {
        for(GenderEnum genderEnum: GenderEnum.values()) {
            if(value.equals(genderEnum.getValue())) return true;
        }
        return false;
    }

}
