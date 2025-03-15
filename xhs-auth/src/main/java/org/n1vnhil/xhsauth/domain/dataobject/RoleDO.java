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

    private Long id;

    private String roleName;

    private String roleKey;

    private Integer status;

    private Integer sort;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean deleted;

}
