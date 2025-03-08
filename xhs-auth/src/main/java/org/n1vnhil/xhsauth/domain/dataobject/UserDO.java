package org.n1vnhil.xhsauth.domain.dataobject;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserDO {
    private Long id;

    private String username;

    private Date createTime;

    private Date updateTime;

}