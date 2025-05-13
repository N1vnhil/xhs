package org.n1vnhil.xhs.count.biz.constant;

public interface MQConstants {

    /**
     * Topic: 关注计数
     */
    String TOPIC_COUNT_FOLLOWING = "CountFollowingCount";

    /**
     * Topic: 粉丝计数
     */
    String TOPIC_COUNT_FANS = "CountFansCount";

    /**
     * Topic: 粉丝计数入库
     */
    String TOPIC_COUNT_FANS_2_DB = "CountFans2DB";

    /**
     * Topic: 关注计数落库
     */
    String TOPIC_COUNT_FOLLOWING_2_DB = "CountFollowing2DBTopic";

    /**
     * Topic: 笔记点赞计数
     */
    String TOPIC_COUNT_NOTE_LIKE = "CountNoteLikeTopic";

    /**
     * Topic: 点赞计数入库
     */
    String TOPIC_COUNT_NOTE_LIKE_2_DB = "CountNoteLike2DBTopic";

    /**
     * Topic: 笔记收藏计数
     */
    String TOPIC_COUNT_NOTE_COLLECT = "CountNoteCollectTopic";

    /**
     * Topic: 收藏计数落库
     */
    String TOPIC_COUNT_NOTE_COLLECT_2_DB = "CountNoteCollect2DBTopic";

    /**
     * Topic: 笔记操作
     */
    String TOPIC_NOTE_OPERATE = "NoteOperateTopic";

    /**
     * Tag: 笔记发布
     */
    String TAG_NOTE_PUBLISH = "publishNote";

    /**
     * Tag: 笔记删除
     */
    String TAG_NOTE_DELETE = "deleteNote";
}
