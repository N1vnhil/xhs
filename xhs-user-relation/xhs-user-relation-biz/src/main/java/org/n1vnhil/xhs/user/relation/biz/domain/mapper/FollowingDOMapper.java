package org.n1vnhil.xhs.user.relation.biz.domain.mapper;

import org.apache.ibatis.annotations.*;
import org.n1vnhil.xhs.user.relation.biz.domain.dataobject.FollowingDO;

import java.util.List;

@Mapper
public interface FollowingDOMapper {

    @Select("select * from t_following where user_id=#{userId}")
    List<FollowingDO> selectFollowUserByUserId(Long userId);

    @Insert("insert into t_following (user_id, following_user_id, create_time) VALUES " +
            "(#{userId}, #{followingUserId}, #{createTime})")
    int insert(FollowingDO followingDO);

    @Delete("delete from t_following where user_id=#{userId} and following_user_id=#{unfollowUserId}")
    int deleteByUserIdAndUnfollowUserId(@Param("userId") Long userId, @Param("unfollowUserId") Long unfollowUserId);
}
