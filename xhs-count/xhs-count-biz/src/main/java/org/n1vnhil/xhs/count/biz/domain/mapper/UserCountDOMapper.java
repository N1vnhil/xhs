package org.n1vnhil.xhs.count.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCountDOMapper {

    int insertOrUpdateFansTotalByUserId(@Param("count") Integer count, @Param("userId") Long userId);

}
