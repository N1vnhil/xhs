package org.n1vnhil.xhsauth.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.n1vnhil.xhsauth.domain.dataobject.UserDO;

@Mapper
public interface UserDOMapper {

    @Select("select id, password from t_user where phone=#{phone}")
    UserDO selectByPhone(String phone);

}
