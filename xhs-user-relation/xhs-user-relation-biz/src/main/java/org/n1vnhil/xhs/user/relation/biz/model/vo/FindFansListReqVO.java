package org.n1vnhil.xhs.user.relation.biz.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindFansListReqVO {

    @NotNull(message = "查询用户id不能为空")
    private Long userId;

    @NotNull(message = "页码不能为空")
    private Integer page = 1;

}
