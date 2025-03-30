package org.n1vnhil.xhs.user.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.n1vnhil.xhs.user.biz.domain.dataobject.UserDO;

import java.util.List;

@Mapper
public interface UserMapper {

    void update(UserDO userDO);

    void insert(UserDO userDO);

    @Select("select * from t_user where phone=#{phone}")
    UserDO selectByPhone(String phone);

    @Select("select * from t_user where id=#{id}")
    UserDO selectById(Long id);

    List<UserDO> selectByIds(@Param("ids") List<Long> ids);
}
