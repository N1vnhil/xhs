package org.n1vnhil.xhs.note.biz.rpc;

import jakarta.annotation.Resource;
import org.n1vnhil.xhs.user.api.UserFeignApi;
import org.n1vnhil.xhs.user.dto.req.FindUserByIdReqDTO;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserRpcService {

    @Resource
    private UserFeignApi userFeignApi;

    public FindUserByIdRspDTO getUserById(Long userId) {
        FindUserByIdReqDTO findUserByIdReqDTO = FindUserByIdReqDTO.builder()
                .id(userId)
                .build();

        FindUserByIdRspDTO response =  userFeignApi.getUserById(findUserByIdReqDTO).getData();

        if(Objects.isNull(response)) return null;
        return response;
    }

}
