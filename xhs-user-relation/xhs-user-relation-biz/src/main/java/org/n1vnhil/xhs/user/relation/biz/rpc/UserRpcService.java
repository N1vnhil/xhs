package org.n1vnhil.xhs.user.relation.biz.rpc;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.api.UserFeignApi;
import org.n1vnhil.xhs.user.dto.req.FindUserByIdReqDTO;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserRpcService {

    @Autowired
    private UserFeignApi userFeignApi;

    public FindUserByIdRspDTO getUserById(Long userId) {
        FindUserByIdReqDTO findUserByIdReqDTO = FindUserByIdReqDTO.builder()
                .id(userId)
                .build();
        Response<FindUserByIdRspDTO> response = userFeignApi.getUserById(findUserByIdReqDTO);
        return (Objects.nonNull(response) && response.isSuccess()) ? response.getData() : null;
    }


}
