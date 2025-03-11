package org.n1vnhil.xhsauth.constant;

public class RedisKeyConstants {

    public static String VERIFICATION_CODE_PREFIX = "verification code:";

    public static String buildVerificationCode(String phone) {
        return VERIFICATION_CODE_PREFIX + phone;
    }

}
