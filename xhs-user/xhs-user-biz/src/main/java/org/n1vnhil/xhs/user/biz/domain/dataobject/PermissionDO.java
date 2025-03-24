package org.n1vnhil.xhs.user.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * Permission DO
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionDO {

    /**
     * 权限id
     */
    private Long id;

    private Long parentId;

    /**
     * 权限名
     */
    private String name;

    /**
     * 权限类型
     */
    private Integer type;

    /**
     * 菜单路由
     */
    private String menuUrl;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 权限排序字段
     */
    private Integer sort;

    /**
     * 权限标识
     */
    private String permissionKey;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean deleted;

}
