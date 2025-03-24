package org.n1vnhil.xhs.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPasswordReqDTO {

    @NotBlank(message = "密码不能为空")
    private String encodedPassword;

}
