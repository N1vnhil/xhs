package org.n1vnhil.xhsauth.service.impl;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.n1vnhil.framework.common.enums.DeletedEnum;
import org.n1vnhil.framework.common.enums.StatusEnum;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhsauth.constant.RedisKeyConstants;
import org.n1vnhil.xhsauth.constant.RoleConstants;
import org.n1vnhil.xhsauth.domain.dataobject.UserDO;
import org.n1vnhil.xhsauth.domain.dataobject.UserRoleDO;
import org.n1vnhil.xhsauth.domain.mapper.UserDOMapper;
import org.n1vnhil.xhsauth.domain.mapper.UserRoleDOMapper;
import org.n1vnhil.xhsauth.enums.ResponseCodeEnum;
import org.n1vnhil.xhsauth.model.vo.user.UserLoginReqVO;
import org.n1vnhil.xhsauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.n1vnhil.xhsauth.enums.LoginTypeEnum;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserRoleDOMapper userRoleDOMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        Integer type = userLoginReqVO.getType();
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(type);

        UserDO user = userDOMapper.selectByPhone(phone);
        Long userId = null;

        switch (loginTypeEnum) {
            case VERIFICATION_CODE -> {
                String verificationCode = userLoginReqVO.getVerificationCode();
                Preconditions.checkArgument(StringUtils.isNotBlank(verificationCode), "验证码不能为空");

                String key = RedisKeyConstants.buildVerificationCode(phone);
                String sentCode = (String) redisTemplate.opsForValue().get(key);

                if(!StringUtils.equals(verificationCode, sentCode)) {
                    return Response.fail(ResponseCodeEnum.VERIFICATION_CODE_WRONG);
                }

                userId = Objects.isNull(user) ? userRegister(phone) : user.getId();

            }

            case PASSWORD -> {
                String password = userLoginReqVO.getPassword();
            }

            default -> {

            }
        }

        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Response.success(tokenInfo.tokenValue);
    }

    /**
     * 用户自动注册
     * @param phone 用户手机号
     * @return 返回注册用户的id
     */
    public Long userRegister(String phone) {
        return transactionTemplate.execute( status -> {
            try {
                Long xhsId = redisTemplate.opsForValue().increment(RedisKeyConstants.XHS_ID_GENERATE_KEY);
                UserDO user = UserDO.builder()
                        .phone(phone)
                        .xhsId(String.valueOf(xhsId))
                        .nickname("小红薯" + xhsId)
                        .status(StatusEnum.ENABLE.getValue())
                        .deleted(DeletedEnum.NO.isDeleted())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                userDOMapper.insert(user);
                Long userId = user.getId();
                log.info("=========== 用户注册：{}, id：{} ===========", phone, userId);

                UserRoleDO role = UserRoleDO.builder()
                        .userId(userId)
                        .roleId(RoleConstants.COMMON_USER_ROLE_ID)
                        .deleted(DeletedEnum.NO.isDeleted())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                userRoleDOMapper.insert(role);

                // 缓存到redis
                List<Long> roles = new ArrayList<>();
                roles.add(RoleConstants.COMMON_USER_ROLE_ID);
                String userRoleKey = RedisKeyConstants.buildVerificationCode(phone);
                redisTemplate.opsForValue().set(userRoleKey, JsonUtils.toJsonString(roles));

                return userId;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("====> 用户注册异常：", e);
                return null;
            }
        });

    }
}

