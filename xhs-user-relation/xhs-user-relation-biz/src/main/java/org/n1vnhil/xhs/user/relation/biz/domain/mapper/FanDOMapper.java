package org.n1vnhil.xhs.user.relation.biz.domain.mapper;

import org.apache.ibatis.annotations.*;
import org.n1vnhil.xhs.user.relation.biz.domain.dataobject.FanDO;

import java.util.List;

@Mapper
public interface FanDOMapper {

    @Insert("insert into t_fans (user_id, fans_user_id, create_time) VALUES " +
            "(#{userId}, #{fanUserId}, #{createTime})")
    int insert(FanDO fanDO);

    @Delete("delete from t_fans where user_id=#{userId} and fans_user_id=#{fansUserId}")
    int deleteByUserIdAndFansUserId(@Param("userId") Long userId, @Param("fansUserId") Long fansUserId);

    @Select("select count(id) from t_fans where user_id=#{userId}")
    int countFansByUserId(@Param("userId") Long userId);

    @Select("select * from t_fans where user_id=#{userId} order by create_time desc limit #{offset}, #{limit}")
    List<FanDO> pageSelect(@Param("userId") Long userId, @Param("offset") Long offset, @Param("limit") Long limit);

    @Select("select * from t_fans where user_id=#{userId} order by create_time desc limit 5000")
    List<FanDO> getAllFansByUserId(Long userId);
}
