package org.n1vnhil.xhsauth.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.n1vnhil.xhsauth.domain.dataobject.PermissionDO;

import java.util.List;

@Mapper
public interface PermissionDOMapper {

    List<PermissionDO> selectAppEnabledList();

}

