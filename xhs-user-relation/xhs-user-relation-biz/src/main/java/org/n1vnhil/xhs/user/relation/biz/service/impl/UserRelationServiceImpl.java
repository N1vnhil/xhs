package org.n1vnhil.xhs.user.relation.biz.service.impl;

import jakarta.annotation.Resource;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;
import org.n1vnhil.xhs.user.api.UserFeignApi;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.n1vnhil.xhs.user.relation.biz.enums.ResponseCodeEnum;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FollowUserReqVO;
import org.n1vnhil.xhs.user.relation.biz.rpc.UserRpcService;
import org.n1vnhil.xhs.user.relation.biz.service.UserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserRelationServiceImpl implements UserRelationService {

    @Resource
    private UserFeignApi userFeignApi;

    @Autowired
    private UserRpcService userRpcService;

    public Response<?> follow(FollowUserReqVO followUserReqVO) {
        Long userId = LoginUserContextHolder.getLoginUserId();
        Long followId = followUserReqVO.getFollowUserId();

        // 校验关注对象是否为自身
        if(Objects.equals(userId, followId)) {
            throw new BizException(ResponseCodeEnum.CANT_FOLLOW_YOUR_SELF);
        }

        // 校验关注对象是否为空
        FindUserByIdRspDTO findUserByIdRspDTO = userRpcService.getUserById(followId);
        if(Objects.isNull(findUserByIdRspDTO)) {
            throw new BizException(ResponseCodeEnum.FOLLOW_USER_NOT_EXISTED);
        }

        return Response.success();
    }

}
