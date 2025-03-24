package org.n1vnhil.xhsauth.service.impl;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.n1vnhil.framework.common.enums.DeletedEnum;
import org.n1vnhil.framework.common.enums.StatusEnum;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.user.dto.resp.FindUserByPhoneRspDTO;
import org.n1vnhil.xhsauth.constant.RedisKeyConstants;
import org.n1vnhil.xhsauth.constant.RoleConstants;
import org.n1vnhil.xhsauth.enums.ResponseCodeEnum;
import org.n1vnhil.xhsauth.filter.LoginUserContextFilter;
import org.n1vnhil.xhsauth.model.vo.user.UpdatePasswordReqVO;
import org.n1vnhil.xhsauth.model.vo.user.UserLoginReqVO;
import org.n1vnhil.xhsauth.rpc.UserRpcService;
import org.n1vnhil.xhsauth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.n1vnhil.xhsauth.enums.LoginTypeEnum;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRpcService userRpcService;

    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        Integer type = userLoginReqVO.getType();
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(type);

        Long userId = null;
        if(Objects.isNull(loginTypeEnum)) {
            return Response.fail(ResponseCodeEnum.LOGIN_TYPE_WRONG);
        }

        switch (loginTypeEnum) {
            case VERIFICATION_CODE -> {
                String verificationCode = userLoginReqVO.getVerificationCode();
                Preconditions.checkArgument(StringUtils.isNotBlank(verificationCode), "验证码不能为空");

                String key = RedisKeyConstants.buildVerificationCode(phone);
                String sentCode = (String) redisTemplate.opsForValue().get(key);

                if(!StringUtils.equals(verificationCode, sentCode)) {
                    return Response.fail(ResponseCodeEnum.VERIFICATION_CODE_WRONG);
                }

                userId = userRpcService.registerUser(phone);
                if(Objects.isNull(userId)) throw new BizException(ResponseCodeEnum.LOGIN_FAIL);

            }

            case PASSWORD -> {
                FindUserByPhoneRspDTO user = userRpcService.findUserByPhone(phone);
                String password = userLoginReqVO.getPassword();
                if(Objects.isNull(user)) {
                    return Response.fail(ResponseCodeEnum.USER_NOT_FOUND);
                }
                String encodedPassword = user.getPassword();

                if(!passwordEncoder.matches(password, encodedPassword)) {
                    return Response.fail(ResponseCodeEnum.PASSWORD_OR_PHONE_WRONG);
                }
                userId = user.getId();
            }

            default -> {

            }
        }

        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Response.success(tokenInfo.tokenValue);
    }

    @Override
    public Response<?> logout(Long userId) {
        StpUtil.logout(userId);
        return Response.success();
    }

    @Override
    public Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO) {
        Long userId = LoginUserContextFilter.getLoginUserId();
        String newPassword = updatePasswordReqVO.getPassword();
        String encodedPassword = passwordEncoder.encode(newPassword);
        userRpcService.updateUserPassword(encodedPassword);
        return Response.success();
    }

}

