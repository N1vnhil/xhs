package org.n1vnhil.xhs.user.relation.biz.constant;

public class RedisKeyConstants {

    private static final String USER_FOLLOWING_KEY_PREFIX = "following:";

    /**
     * 构造用户关注列表redis key
     * @param userId
     * @return
     */
    public static String buildUserFollowingKey(Long userId) {
        return USER_FOLLOWING_KEY_PREFIX + userId;
    }

}
