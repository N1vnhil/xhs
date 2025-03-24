package org.n1vnhil.xhs.user.biz.service;


import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.biz.model.vo.UpdateUserReqVO;
import org.n1vnhil.xhs.user.dto.req.FindUserByPhoneReqDTO;
import org.n1vnhil.xhs.user.dto.req.RegisterUserReqDTO;
import org.n1vnhil.xhs.user.dto.resp.FindUserByPhoneRspDTO;

public interface UserService {

    /**
     * 更新用户信息
     * @param updateUserReqVO
     * @return
     */
    Response<?> updateUserInfo(UpdateUserReqVO updateUserReqVO);

    /**
     * 用户注册
     * @return
     */
    Response<Long> register(RegisterUserReqDTO registerUserReqDTO);

    /**
     * 根据手机号查询用户
     * @param findUserByPhoneReqDTO
     * @return
     */
    Response<FindUserByPhoneRspDTO> findUserByPhone(FindUserByPhoneReqDTO findUserByPhoneReqDTO);
}
