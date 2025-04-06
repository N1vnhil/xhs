package org.n1vnhil.xhs.note.biz.constant;

public class RedisKeyConstants {

    public static final String NOTE_DETAIL_KEY = "note:detail:";

    public static String buildNoteDetailKey(Long noteId) {
       return NOTE_DETAIL_KEY + noteId;
    }

    public static final String BLOOM_USER_NOTE_LIKE_LIST_KEY = "bloom:note:likes:";

    public static String buildBloomUserNoteLikeListKey(Long noteId) {
        return BLOOM_USER_NOTE_LIKE_LIST_KEY + noteId;
    }
}
