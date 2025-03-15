package org.n1vnhil.xhsauth.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * Role DO
 * */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDO {

    /**
     * 角色id
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 角色状态
     * 1 禁用
     * 0 启用
     */
    private Integer status;

    /**
     * 角色排序字段
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean deleted;

}
