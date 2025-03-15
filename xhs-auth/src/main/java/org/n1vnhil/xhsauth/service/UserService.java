package org.n1vnhil.xhsauth.service;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.model.vo.user.UserLoginReqVO;

public interface UserService {

    Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO);

}
