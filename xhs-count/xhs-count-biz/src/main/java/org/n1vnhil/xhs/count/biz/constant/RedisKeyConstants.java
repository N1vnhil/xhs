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

    /**
     * 笔记计数 key 前缀
     */
    private static final String COUNT_NOTE_KEY_PREFIX = "count:note";

    /**
     * Hash Field: 点赞总数
     */
    public static final String FIELD_LIKE_TOTAL = "likeTotal";

    /**
     * 获取笔记计数 key
     * @param noteId
     * @return
     */
    private static String buildCountNoteKey(Long noteId) {
        return COUNT_NOTE_KEY_PREFIX + noteId;
    }
}
