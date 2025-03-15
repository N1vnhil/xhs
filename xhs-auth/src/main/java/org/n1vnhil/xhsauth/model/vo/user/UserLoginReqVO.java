package org.n1vnhil.xhsauth.model.vo.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.n1vnhil.framework.common.validator.PhoneNumber;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserLoginReqVO {
    /**
     * 手机号
     * */
    @PhoneNumber
    @NotBlank(message = "手机号不能为空")
    private String phone;


    /**
     * 验证码
     * */
    private String verificationCode;

    /**
     * 密码
     * */
    private String password;

    /**
     * 登录方式，用户可选择通过密码或验证码登录
     * */
    @NotNull(message = "登录方式不能为空")
    private Integer type;
}
