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
import org.n1vnhil.xhsauth.domain.dataobject.RoleDO;
import org.n1vnhil.xhsauth.domain.dataobject.UserDO;
import org.n1vnhil.xhsauth.domain.dataobject.UserRoleDO;
import org.n1vnhil.xhsauth.domain.mapper.RoleDOMapper;
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
    private RoleDOMapper roleDOMapper;

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

                userId = Objects.isNull(user) ? registerUser(phone) : user.getId();

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
    private Long registerUser(String phone) {
        return transactionTemplate.execute(status -> {
            try {
                // 获取全局自增的小哈书 ID
                Long xiaohashuId = redisTemplate.opsForValue().increment(RedisKeyConstants.XHS_ID_GENERATE_KEY);

                UserDO userDO = UserDO.builder()
                        .phone(phone)
                        .xhsId(String.valueOf(xiaohashuId)) // 自动生成小红书号 ID
                        .nickname("小红薯" + xiaohashuId) // 自动生成昵称, 如：小红薯10000
                        .status(StatusEnum.ENABLE.getValue()) // 状态为启用
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .deleted(DeletedEnum.NO.isDeleted()) // 逻辑删除
                        .build();

                // 添加入库
                userDOMapper.insert(userDO);

                // 获取刚刚添加入库的用户 ID
                Long userId = userDO.getId();

                // 给该用户分配一个默认角色
                UserRoleDO userRoleDO = UserRoleDO.builder()
                        .userId(userId)
                        .roleId(RoleConstants.COMMON_USER_ROLE_ID)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .deleted(DeletedEnum.NO.isDeleted())
                        .build();
                userRoleDOMapper.insert(userRoleDO);

                RoleDO roleDO = roleDOMapper.selectById(RoleConstants.COMMON_USER_ROLE_ID);

                // 将该用户的角色 ID 存入 Redis 中
                List<String> roles = new ArrayList<>(1);
                roles.add(roleDO.getRoleKey());

                String userRolesKey = RedisKeyConstants.buildUserRoleKey(userId);
                redisTemplate.opsForValue().set(userRolesKey, JsonUtils.toJsonString(roles));

                return userId;
            } catch (Exception e) {
                status.setRollbackOnly(); // 标记事务为回滚
                log.error("==> 系统注册用户异常: ", e);
                return null;
            }
        });
    }

    @Override
    public Response<?> logout(Long userId) {
        StpUtil.logout(userId);
        return Response.success();
    }
}

