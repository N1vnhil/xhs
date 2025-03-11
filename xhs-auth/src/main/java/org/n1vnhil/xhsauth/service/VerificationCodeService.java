package org.n1vnhil.xhsauth.service;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.model.vo.verificationcode.SendVerificationCodeRequestCodeVO;

public interface VerificationCodeService {

    /**
     * @param sendVerificationCodeRequestCodeVO 发送验证码VO
     * 根据用户手机号发送验证码，redis缓存三分钟
     * */
    public Response<?> sendVerificationCode(SendVerificationCodeRequestCodeVO sendVerificationCodeRequestCodeVO);


}
