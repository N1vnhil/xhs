package org.n1vnhil.xhsauth.model.vo.verificationcode;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeRequestCodeVO {

    @NotNull(message = "手机号不能为空")
    private String phone;

}
