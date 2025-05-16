package org.n1vnhil.xhs.data.align.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UpdateMapper {

    int updateUserFollowingTotalByUser(@Param("userId") Long userId,
                                       @Param("followingTotal") Integer followingTotal);

    int updateNoteLikeTotalByNoteId(@Param("userId") Long noteId,
                                       @Param("followingTotal") Integer noteLikeTotal);

}
