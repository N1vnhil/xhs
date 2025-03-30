package org.n1vnhil.xhs.user.biz.service;


import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.biz.model.vo.UpdateUserReqVO;
import org.n1vnhil.xhs.user.dto.req.*;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.n1vnhil.xhs.user.dto.resp.FindUserByPhoneRspDTO;

import java.util.List;

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

    /**
     * 修改密码
     * @param updateUserPasswordReqDTO
     * @return
     */
    Response<?> editPassword(UpdateUserPasswordReqDTO updateUserPasswordReqDTO);

    /**
     * 根据用户id查询用户
     * @param findUserByIdReqDTO
     * @return
     */
    Response<FindUserByIdRspDTO> findUserById(FindUserByIdReqDTO findUserByIdReqDTO);

    /**
     * 用户批量查询
     * @param findUserByIdsReqDTO
     * @return
     */
    Response<List<FindUserByIdRspDTO>> findUserByIds(FindUserByIdsReqDTO findUserByIdsReqDTO);

}
