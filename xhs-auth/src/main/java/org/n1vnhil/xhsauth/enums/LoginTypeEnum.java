package org.n1vnhil.xhsauth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    VERIFICATION_CODE(1),
    PASSWORD(2);

    private final Integer value;

    public static LoginTypeEnum valueOf(Integer value) {
        for(LoginTypeEnum loginTypeEnum: LoginTypeEnum.values()) {
            if(value.equals(loginTypeEnum.value)) return loginTypeEnum;
        }
        return null;
    }
}
