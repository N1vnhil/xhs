package org.n1vnhil.xhs.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.n1vnhil.framework.common.validator.PhoneNumber;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserByPhoneReqDTO {

    @NotBlank(message = "手机号不能为空")
    @PhoneNumber
    String phone;

}
