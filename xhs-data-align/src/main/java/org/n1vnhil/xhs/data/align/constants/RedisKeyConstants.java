package org.n1vnhil.xhs.data.align.constants;

public class RedisKeyConstants {

    public static final String BLOOM_TODAY_NOTE_LIKE_LIST_KEY = "bloom:dataAlign:note:likes";

    public static String buildBloomUserNoteLikeListKey(String date) {
        return BLOOM_TODAY_NOTE_LIKE_LIST_KEY + date;
    }

    public static final String BLOOM_TODAY_NOTE_COLLECT_LIST_KEY = "bloom:dataAlign:note:collects";

    public static String buildBloomUserNoteCollectListKey(String date) {
        return BLOOM_TODAY_NOTE_COLLECT_LIST_KEY + date;
    }

    public static final String BLOOM_TODAY_USER_NOTE_OPERATE_LIST_KEY = "bloom:dataAlign:user:note:operators:";

    public static String buildBloomUserNoteOperateListKey(String date) {
        return BLOOM_TODAY_USER_NOTE_OPERATE_LIST_KEY + date;
    }

    public static final String BLOOM_TODAY_USER_FOLLOW_LIST_KEY = "bloom:dataAlign:user:follow:";

    public static String buildBloomUserFollowListKey(String date) {
        return BLOOM_TODAY_USER_FOLLOW_LIST_KEY + date;
    }

    private static final String COUNT_USER_KEY_PREFIX = "count:user:";

    public static final String FIELD_FOLLOWING_TOTAL = "followingTotal";

    public static String buildCountUserKey(Long userId) {
        return COUNT_USER_KEY_PREFIX + userId;
    }

}
