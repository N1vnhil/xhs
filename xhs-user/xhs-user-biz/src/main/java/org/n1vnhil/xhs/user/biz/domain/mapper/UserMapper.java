package org.n1vnhil.xhs.user.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.n1vnhil.xhs.user.biz.domain.dataobject.UserDO;

@Mapper
public interface UserMapper {

    void update(UserDO userDO);

    void insert(UserDO userDO);

    @Select("select * from t_user where phone=#{phone}")
    UserDO selectByPhone(String phone);
}
