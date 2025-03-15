package org.n1vnhil.xhsauth.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.n1vnhil.xhsauth.domain.dataobject.RoleDO;

import java.util.List;

@Mapper
public interface RoleDOMapper {

    /**
     * 查询所有启用角色
     * */
    List<RoleDO> selectEnalbedRoles();

}
