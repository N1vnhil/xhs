package org.n1vnhil.xhsauth.domain.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.n1vnhil.xhsauth.domain.dataobject.UserDO;

@Mapper
public interface UserDOMapper {

    @Insert("insert into t_user (id, username, create_time, update_time) values(#{id}, #{username}, #{createTime}, #{updateTime})")
    int insert(UserDO record);

    @Select("select * from t_user where id=#{id}")
    UserDO selectByPrimaryKey(Long id);

}