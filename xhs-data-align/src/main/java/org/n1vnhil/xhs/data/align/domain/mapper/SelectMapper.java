package org.n1vnhil.xhs.data.align.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Mapper
public interface SelectMapper {

    List<Long> selectBatchFromDataAlignFollowingCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("batchSize") int batchSize);

    int selectCountFromFollowingTableByUserId(@Param("userId") Long userId);

    List<Long> selectBatchFromDataAlignNoteLikeCountTempTable(@Param("tableNameSuffix") String tableNameSuffix, @Param("batchSize") int batchSize);

    int selectCountFromLikeTableByNoteId(long noteId);

}
