package org.n1vnhil.xhs.note.biz.domain.mapper;


import org.apache.ibatis.annotations.Param;

public interface NoteCollectionDOMapper {

    int selectCountByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId);

}
