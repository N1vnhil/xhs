package org.n1vnhil.xhs.user.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.n1vnhil.xhs.user.biz.domain.dataobject.RolePermissionDO;

import java.util.List;


@Mapper
public interface RolePermissionDOMapper {

    /**
     * 根据角色id查询权限
     * @param roleIds 角色id
     * @return 角色id，权限id
     */
    List<RolePermissionDO> selectByRoleIds(@Param("roleIds") List<Long> roleIds);

}
