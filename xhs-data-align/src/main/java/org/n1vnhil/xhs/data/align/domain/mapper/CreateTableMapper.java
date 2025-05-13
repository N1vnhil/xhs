package org.n1vnhil.xhs.data.align.domain.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CreateTableMapper {

    /**
     * 创建日增量表：关注数计数变更
     * @param tableNameSuffix
     */
    void createDataAlignFollowingCountTemplateTable(String tableNameSuffix);

    /**
     * 创建日增量表：粉丝数计数变更
     * @param tableNameSuffix
     */
    void createDataAlignFansCountTemplateTable(String tableNameSuffix);

    /**
     * 创建日增量表：笔记收藏数计数变更
     * @param tableNameSuffix
     */
    void createDataAlignNoteCollectCountTemplateTable(String tableNameSuffix);

    /**
     * 创建日增量表：笔记点赞数变更
     * @param tableNameSuffix
     */
    void createDataAlignNoteLikeCountTemplateTable(String tableNameSuffix);

    /**
     * 创建日增量表：用户收藏数变更
     * @param tableNameSuffix
     */
    void createDataAlignUserCollectCountTemplateTable(String tableNameSuffix);

    /**
     * 创建日增量表：用户点赞数数变更
     * @param tableNameSuffix
     */
    void createDataAlignUserLikeCountTemplateTable(String tableNameSuffix);

    /**
     * 创建日增量表：笔记发布数计数变更
     * @param tableNameSuffix
     */
    void createDataAlignNotePublishCountTemplateTable(String tableNameSuffix);

}
