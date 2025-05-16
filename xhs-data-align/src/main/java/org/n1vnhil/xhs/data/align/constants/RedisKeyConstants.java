package org.n1vnhil.xhs.data.align.constants;

public class RedisKeyConstants {

    public static final String BLOOM_TODAY_NOTE_LIKE_USER_ID_LIST_KEY = "bloom:dataAlign:note:likes:userIds";
    public static final String BLOOM_TODAY_NOTE_LIKE_NOTE_ID_LIST_KEY = "bloom:dataAlign:note:likes:noteIds";

    public static String buildBloomNoteLikeUserIdListKey(String date) {
        return BLOOM_TODAY_NOTE_LIKE_USER_ID_LIST_KEY + date;
    }

    public static String buildBloomNoteLikeNoteIdListKey(String date) {
        return BLOOM_TODAY_NOTE_LIKE_NOTE_ID_LIST_KEY + date;
    }

    public static final String BLOOM_TODAY_NOTE_COLLECT_USER_ID_LIST_KEY = "bloom:dataAlign:note:collects:userIds";
    public static final String BLOOM_TODAY_NOTE_COLLECT_NOTE_ID_LIST_KEY = "bloom:dataAlign:note:collects:noteIds";

    public static String buildBloomUserNoteCollectUserIdsListKey(String date) {
        return BLOOM_TODAY_NOTE_COLLECT_USER_ID_LIST_KEY + date;
    }

    public static String buildBloomUserNoteCollectNoteIdsListKey(String date) {
        return BLOOM_TODAY_NOTE_COLLECT_NOTE_ID_LIST_KEY + date;
    }

    public static final String BLOOM_TODAY_USER_NOTE_OPERATE_LIST_KEY = "bloom:dataAlign:user:note:operators:";

    public static String buildBloomUserNoteOperateListKey(String date) {
        return BLOOM_TODAY_USER_NOTE_OPERATE_LIST_KEY + date;
    }

    public static final String BLOOM_TODAY_USER_FOLLOW_FANS_LIST_KEY = "bloom:dataAlign:user:follow:fans";
    public static final String BLOOM_TODAY_USER_FOLLOW_FOLLOWING_LIST_KEY = "bloom:dataAlign:user:follow:following";


    public static String buildBloomUserFollowFansListKey(String date) {
        return BLOOM_TODAY_USER_FOLLOW_FANS_LIST_KEY + date;
    }

    public static String buildBloomUserFollowFollowingListKey(String date) {
        return BLOOM_TODAY_USER_FOLLOW_FOLLOWING_LIST_KEY + date;
    }

    private static final String COUNT_USER_KEY_PREFIX = "count:user:";

    public static final String FIELD_FOLLOWING_TOTAL = "followingTotal";

    public static String buildCountUserKey(Long userId) {
        return COUNT_USER_KEY_PREFIX + userId;
    }

}
