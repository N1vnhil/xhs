package org.n1vnhil.xhsauth.model.vo.verificationcode;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.n1vnhil.framework.common.validator.PhoneNumber;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeReqCodeVO {

    @PhoneNumber
    @NotNull(message = "手机号不能为空")
    private String phone;

}
