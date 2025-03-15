package org.n1vnhil.xhsauth.service.impl;


import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.constant.RedisKeyConstants;
import org.n1vnhil.xhsauth.domain.dataobject.UserDO;
import org.n1vnhil.xhsauth.domain.mapper.UserDOMapper;
import org.n1vnhil.xhsauth.enums.ResponseCodeEnum;
import org.n1vnhil.xhsauth.model.vo.user.UserLoginReqVO;
import org.n1vnhil.xhsauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.n1vnhil.xhsauth.enums.LoginTypeEnum;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserDOMapper userDOMapper;

    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        Integer type = userLoginReqVO.getType();
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(type);

        UserDO user = userDOMapper.selectByPhone(phone);

        switch (loginTypeEnum) {
            case VERIFICATION_CODE -> {
                String verificationCode = userLoginReqVO.getVerificationCode();
                if(StringUtils.isBlank(verificationCode)) {
                    return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
                }

                String key = RedisKeyConstants.buildVerificationCode(phone);
                String sentCode = (String) redisTemplate.opsForValue().get(key);

                if(!StringUtils.equals(verificationCode, sentCode)) {
                    return Response.fail(ResponseCodeEnum.VERIFICATION_CODE_WRONG);
                }

                if(Objects.isNull(user)) {
                    // TODO: 用户未注册，自动注册
                } else {
                    // TODO: 用户已注册
                }

            }

            case PASSWORD -> {
                String password = userLoginReqVO.getPassword();
            }

            default -> {

            }
        }

        return null;
    }
}

