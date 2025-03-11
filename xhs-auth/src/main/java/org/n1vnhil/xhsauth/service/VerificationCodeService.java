package org.n1vnhil.xhsauth.service;

import org.n1vnhil.framework.common.response.Response;

public interface VerificationCodeService {

    /**
     * @param phone 用户手机号
     * 根据用户手机号发送验证码，redis缓存三分钟
     * */
    public Response<String> sendVerificationCode(String phone);


}
