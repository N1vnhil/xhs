package org.n1vnhil.xhs.data.align.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InsertMapper {

    /**
     * 笔记点赞计数
     * @param tableNameSuffix
     * @param noteId
     */
    void insert2DataAlignNoteLikeCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("noteId") Long noteId);

    /**
     * 用户点赞计数
     * @param tableNameSuffix
     * @param userId
     */
    void insert2DataAlignUserLikeCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("userId") Long userId);


    /**
     * 笔记收藏计数
     * @param tableNameSuffix
     * @param noteId
     */
    void insert2DataAlignNoteCollectCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("noteId") Long noteId);

    /**
     * 用户收藏计数
     * @param tableNameSuffix
     * @param userId
     */
    void insert2DataAlignUserCollectCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("userId") Long userId);

    /**
     * 用户笔记发布计数
     * @param tableNameSuffix
     * @param userId
     */
    void insert2DataAlignUserPublishCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("userId") Long userId);

    /**
     * 用户关注计数
     */
    void insert2DataAlignUserFollowingCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("userId") Long userId);

    /**
     * 用户粉丝计数
     */
    void insert2DataAlignUserFansCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("userId") Long userId);

}
