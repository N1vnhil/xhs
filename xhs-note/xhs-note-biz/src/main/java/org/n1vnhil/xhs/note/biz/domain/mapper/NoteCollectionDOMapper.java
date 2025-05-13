package org.n1vnhil.xhs.note.biz.domain.mapper;


import org.apache.ibatis.annotations.Param;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteCollectionDO;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteDO;

import java.util.List;

public interface NoteCollectionDOMapper {

    int selectCountByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId);

    List<NoteCollectionDO> selectByUserId(@Param("userId") Long userId);

    List<NoteCollectionDO> selectCollectedByUserIdAndLimit(@Param("userId") Long userId, @Param("limit") Long limit);

    int insertOrUpdate(NoteCollectionDO noteCollectionDO);

    int update2UncollectByUserIdAndNoteId(NoteCollectionDO noteCollectionDO);

}
