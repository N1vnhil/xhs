package org.n1vnhil.xhs.count.biz.constant;

import org.n1vnhil.framework.common.response.PageResponse;

public class RedisKeyConstants {

    /**
     * 用户维度计数 Key 前缀
     */
    private static final String COUNT_USER_KEY_PREFIX = "count:user:";

    public static final String FIELD_FANS_TOTAL = "fansTotal";

    /**
     * 构造用户维度计数 redis key
     * @param userId
     * @return
     */
    public static String buildCountUserKey(Long userId) {
        return COUNT_USER_KEY_PREFIX + userId;
    }

    public static String FIELD_FOLLOWING_TOTAL = "followingTotal";

}
