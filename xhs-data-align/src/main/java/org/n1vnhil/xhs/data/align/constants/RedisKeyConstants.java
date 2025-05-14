package org.n1vnhil.xhs.data.align.constants;

public class RedisKeyConstants {

    public static final String BLOOM_TODAY_NOTE_LIKE_LIST_KEY = "bloom:dataAlign:note:likes";

    public static String buildBloomUserNoteLikeListKey(String date) {
        return BLOOM_TODAY_NOTE_LIKE_LIST_KEY + date;
    }

}
