package org.n1vnhil.xhs.user.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.n1vnhil.xhs.user.biz.domain.dataobject.UserDO;

@Mapper
public interface UserMapper {

    void update(UserDO userDO);

}
