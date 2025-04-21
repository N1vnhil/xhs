package org.n1vnhil.xhs.count.biz.constant;

public interface MQConstants {

    /**
     * 关注计数 Topic
     */
    String TOPIC_COUNT_FOLLOWING = "CountFollowingCount";

    /**
     * 粉丝计数 Topic
     */
    String TOPIC_COUNT_FANS = "CountFansCount";

    /**
     * 粉丝计数入库 Topic
     */
    String TOPIC_COUNT_FANS_2_DB = "CountFans2DB";

    String TOPIC_COUNT_FOLLOWING_2_DB = "CountFollowing2DBTopic";

    /**
     * 笔记点赞计数 Topic
     */
    String TOPIC_COUNT_NOTE_LIKE = "CountNoteLikeTopic";
}
