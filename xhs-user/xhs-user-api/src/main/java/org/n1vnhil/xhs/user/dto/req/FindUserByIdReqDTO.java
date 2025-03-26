package org.n1vnhil.xhs.user.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindUserByIdReqDTO {

    @NotNull(message = "用户id不能为空.")
    private Long id;

}
