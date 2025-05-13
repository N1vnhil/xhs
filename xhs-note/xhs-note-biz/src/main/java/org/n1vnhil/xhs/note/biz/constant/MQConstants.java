package org.n1vnhil.xhs.note.biz.constant;


public interface MQConstants {

    /**
     * Topic: 删除本地缓存
     */
    String TOPIC_DELETE_NOTE_LOCAL_CACHE ="DeleteLocalCacheTopic";

    /**
     * Topic: 点赞、取消赞
     */
    String TOPIC_LIKE_OR_UNLIKE = "LikeUnlikeTopic";

    /**
     * 标签：点赞
     */
    String TAG_LIKE = "Like";

    /**
     * 标签：取消赞
     */
    String TAG_UNLIKE = "Unlike";

    /**
     * Topic: 计数 - 笔记点赞
     */
    String TOPIC_COUNT_NOTE_LIKE = "CountNoteLikeTopic";

    /**
     * Topic: 计数 - 笔记收藏
     */
    String TOPIC_COUNT_NOTE_COLLECT = "CountNoteCollectTopic";

    /**
     * Topic: 收藏 / 取消收藏
     */
    String TOPIC_COLLECT_OR_UNCOLLECT = "CollectUncollectTopic";

    /**
     * Tag: 收藏
     */
    String TAG_COLLECT = "Collect";

    /**
     * Tag: 取消收藏
     */
    String TAG_UNCOLLECT = "Uncollect";

    /**
     * Topic: 笔记操作
     */
    String TOPIC_NOTE_OPERATE = "NoteOperateTopic";

    /**
     * Tag: 发布笔记
     */
    String TAG_NOTE_PUBLISH = "publishNote";

    /**
     * Tag: 删除笔记
     */
    String TAG_NOTE_DELETE = "deleteNote";
}
