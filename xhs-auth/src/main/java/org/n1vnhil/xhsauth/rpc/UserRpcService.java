package org.n1vnhil.xhsauth.rpc;

import jakarta.annotation.Resource;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.api.UserFeignApi;
import org.n1vnhil.xhs.user.dto.req.FindUserByPhoneReqDTO;
import org.n1vnhil.xhs.user.dto.req.RegisterUserReqDTO;
import org.n1vnhil.xhs.user.dto.resp.FindUserByPhoneRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRpcService {

    @Resource
    private UserFeignApi userFeignApi;

    /**
     * 用户注册
     * @param phone
     * @return
     */
    public Long registerUser(String phone) {
        RegisterUserReqDTO registerUserReqDTO = RegisterUserReqDTO.builder()
                .phone(phone)
                .build();
        Response<Long> response = userFeignApi.registerUser(registerUserReqDTO);
        return response.isSuccess() ? response.getData() : null;
    }

    public FindUserByPhoneRspDTO findUserByPhone(String phone) {
        FindUserByPhoneReqDTO findUserByPhoneReqDTO = FindUserByPhoneReqDTO.builder()
                .phone(phone)
                .build();
        Response<FindUserByPhoneRspDTO> response = userFeignApi.findByPhone(findUserByPhoneReqDTO);
        return response.isSuccess() ? response.getData() : null;
    }
}
