package org.n1vnhil.xhs.note.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteLikeDO;

import java.util.List;

@Mapper
public interface NoteLikeDOMapper {

    @Select("select count(1) from t_note_like where user_id=#{userId} and note_id=#{noteId} and status=1 limit 1")
    int selectCountByUserIdAndNoteId(@Param("userId") Long userId, @Param("noteId") Long noteId);

    @Select("select * from t_note_like where user_id=#{userId} and status=1")
    List<NoteLikeDO> selectByUserId(@Param("userId") Long userId);

    @Select("select * from t_note_like where user_id=#{userId} and status=1 order by create_time desc limit #{limit}")
    List<NoteLikeDO> selectByUserIdAndLimit(@Param("userId") Long userId, @Param("limit") Integer limit);

    int updateStatusByNoteIdAndUserId(NoteLikeDO noteLikeDO);

    int insertOrUpadte(NoteLikeDO noteLikeDO);

}
