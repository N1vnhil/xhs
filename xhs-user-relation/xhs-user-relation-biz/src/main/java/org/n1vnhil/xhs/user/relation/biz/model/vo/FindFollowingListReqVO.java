package org.n1vnhil.xhs.user.relation.biz.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindFollowingListReqVO {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotNull(message = "分页不能为空")
    private Integer page = 1;

}
