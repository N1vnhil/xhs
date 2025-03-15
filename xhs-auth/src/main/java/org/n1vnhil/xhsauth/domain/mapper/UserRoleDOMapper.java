package org.n1vnhil.xhsauth.domain.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.n1vnhil.xhsauth.domain.dataobject.UserRoleDO;

@Mapper
public interface UserRoleDOMapper {

    @Insert("insert into t_user_role_rel (user_id, role_id, create_time, update_time, is_deleted) " +
            "values (#{userId}, #{roleId}, #{updateTime}, #{createTime}, #{delete})")
    void insert(UserRoleDO userRoleDO);

}
