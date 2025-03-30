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
public class UnfollowUserReqDTO {

    @NotNull(message = "被取关用户id不能为空")
    private Long unfollowUserId;

}
