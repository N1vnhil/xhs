package org.n1vnhil.xhsauth.service;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.model.vo.user.UpdatePasswordReqVO;
import org.n1vnhil.xhsauth.model.vo.user.UserLoginReqVO;

public interface AuthService {

    Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO);

    Response<?> logout(Long userId);

    Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO);

}
