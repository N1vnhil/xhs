package org.n1vnhil.xhsauth.constant;

public class RedisKeyConstants {

    public static String VERIFICATION_CODE_PREFIX = "verification code:";

    public static String buildVerificationCode(String phone) {
        return VERIFICATION_CODE_PREFIX + phone;
    }

    /**
     * xhs全局id生成redis key
     */
    public static final String XHS_ID_GENERATE_KEY = "xhs_id_generator";

    /**
     * 用户角色前缀
     */
    private static final String USER_ROLE_KEY_PREFIX = "user:roles:";

    /**
     * 根据用户手机号构造用户角色redis key
     * @param phone 用户手机号
     * @return 用户角色redis key
     */
    private static String buildUserRoleKey(String phone) {
        return USER_ROLE_KEY_PREFIX + phone;
    }

    private static final String ROLE_PERMISSIONS_KEY_PREFIX = "role:permissions:";

    public static String buildRolePermissionKey(Long roleId) {
        return ROLE_PERMISSIONS_KEY_PREFIX + roleId;
    }
}
