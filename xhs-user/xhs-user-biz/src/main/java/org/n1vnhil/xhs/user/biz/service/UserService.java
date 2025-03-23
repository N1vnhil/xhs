package org.n1vnhil.xhs.user.biz.service;


import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.biz.model.vo.UpdateUserReqVO;

public interface UserService {

    /**
     * 更新用户信息
     * @param updateUserReqVO
     * @return
     */
    Response<?> updateUserInfo(UpdateUserReqVO updateUserReqVO);

}
