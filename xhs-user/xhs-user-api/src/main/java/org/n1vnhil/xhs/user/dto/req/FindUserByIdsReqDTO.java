package org.n1vnhil.xhs.user.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindUserByIdsReqDTO {

    @NotNull(message = "用户 id 不能为空")
    @Size(min = 1, max = 10, message = "用户 id 集合大小必须大于1，小于10")
    private List<Long> ids;

}
