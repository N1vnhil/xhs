package org.n1vnhil.xhsauth.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;


/**
 * 角色-权限关系DO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolePermissionDO {

    private Long id;

    private Long roleId;

    private Long permissionId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean deleted;
}
