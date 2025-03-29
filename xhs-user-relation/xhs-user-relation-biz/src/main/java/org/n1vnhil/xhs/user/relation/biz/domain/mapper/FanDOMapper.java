package org.n1vnhil.xhs.user.relation.biz.domain.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.n1vnhil.xhs.user.relation.biz.domain.dataobject.FanDO;

@Mapper
public interface FanDOMapper {

    @Insert("insert into t_fans (user_id, fans_user_id, create_time) VALUES " +
            "(#{userId}, #{fanUserId}, #{createTime})")
    int insert(FanDO fanDO);

}
