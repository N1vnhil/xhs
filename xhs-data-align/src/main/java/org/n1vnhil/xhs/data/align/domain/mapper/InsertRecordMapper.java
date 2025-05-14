package org.n1vnhil.xhs.data.align.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InsertRecordMapper {

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

}
