package org.n1vnhil.xhs.count.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoteCountDOMapper {

    int insertOrUpdateLikeTotalById(@Param("count") Integer count, @Param("noteId") Long noteId);

    int insertOrUpdateCollectTotalById(@Param("count") Integer count, @Param("noteId") Long noteId);
}
