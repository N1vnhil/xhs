package org.n1vnhil.xhs.user.biz.service.impl;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n1vnhil.framework.common.enums.DeletedEnum;
import org.n1vnhil.framework.common.enums.StatusEnum;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.framework.common.util.ParamUtils;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;
import org.n1vnhil.xhs.user.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.user.biz.constant.RoleConstants;
import org.n1vnhil.xhs.user.biz.domain.dataobject.RoleDO;
import org.n1vnhil.xhs.user.biz.domain.dataobject.UserDO;
import org.n1vnhil.xhs.user.biz.domain.dataobject.UserRoleDO;
import org.n1vnhil.xhs.user.biz.domain.mapper.RoleDOMapper;
import org.n1vnhil.xhs.user.biz.domain.mapper.UserMapper;
import org.n1vnhil.xhs.user.biz.domain.mapper.UserRoleDOMapper;
import org.n1vnhil.xhs.user.biz.enums.GenderEnum;
import org.n1vnhil.xhs.user.biz.enums.ResponseCodeEnum;
import org.n1vnhil.xhs.user.biz.model.vo.UpdateUserReqVO;
import org.n1vnhil.xhs.user.biz.rpc.DistributedIdGeneratorRpcService;
import org.n1vnhil.xhs.user.biz.rpc.OssRpcService;
import org.n1vnhil.xhs.user.biz.service.UserService;
import org.n1vnhil.xhs.user.dto.req.FindUserByIdReqDTO;
import org.n1vnhil.xhs.user.dto.req.FindUserByPhoneReqDTO;
import org.n1vnhil.xhs.user.dto.req.RegisterUserReqDTO;
import org.n1vnhil.xhs.user.dto.req.UpdateUserPasswordReqDTO;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.n1vnhil.xhs.user.dto.resp.FindUserByPhoneRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OssRpcService ossRpcService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRoleDOMapper userRoleDOMapper;

    @Autowired
    private RoleDOMapper roleDOMapper;

    @Resource
    private DistributedIdGeneratorRpcService distributedIdGeneratorRpcService;

    /**
     * 更新用户信息
     * @param updateUserReqVO
     * @return
     */
    @Override
    public Response<?> updateUserInfo(UpdateUserReqVO updateUserReqVO) {
        UserDO user = new UserDO();
        user.setId(LoginUserContextHolder.getLoginUserId());
        boolean needUpdate = false;

        // avatar
        MultipartFile avatarFile = updateUserReqVO.getAvatar();
        if(Objects.nonNull(avatarFile)) {
            String avatar = ossRpcService.uploadFile(avatarFile);
            if(StringUtils.isBlank(avatar)) throw new BizException(ResponseCodeEnum.UPLOAD_AVATAR_FAIL);
            user.setAvatar(avatar);
            needUpdate = true;
        }

        // nickname
        String nickname = updateUserReqVO.getNickname();
        if(StringUtils.isNotBlank(nickname)) {
            Preconditions.checkArgument(ParamUtils.checkNickname(nickname), ResponseCodeEnum.NICK_NAME_VALID_FAIL.getErrorMessage());
            needUpdate = true;
            user.setNickname(nickname);
        }

        // xhsId
        String xhsId = updateUserReqVO.getXhsId();
        if(StringUtils.isNotBlank(xhsId)) {
            Preconditions.checkArgument(ParamUtils.checkXhsId(xhsId), ResponseCodeEnum.XHS_ID_VALID_FAIL.getErrorMessage());
            needUpdate = true;
            user.setXhsId(xhsId);
        }

        // birthday
        LocalDate birthday = updateUserReqVO.getBirthday();
        if(Objects.nonNull(birthday)) {
            needUpdate = true;
            user.setBirthday(birthday);
        }

        // backgroundImg
        MultipartFile backgroundImgFile = updateUserReqVO.getBackgroundImg();
        if(Objects.nonNull(backgroundImgFile)) {
            String backgroundImg = ossRpcService.uploadFile(backgroundImgFile);
            if(StringUtils.isBlank(backgroundImg)) throw new BizException(ResponseCodeEnum.UPLOAD_BACKGROUND_IMG_FAIL);
            user.setBackgroundImg(backgroundImg);
            needUpdate = true;
        }

        // sex
        Integer sex = updateUserReqVO.getSex();
        if(Objects.nonNull(sex)) {
            Preconditions.checkArgument(GenderEnum.valid(sex), ResponseCodeEnum.SEX_VALID_FAIL.getErrorMessage());
            needUpdate = true;
            user.setSex(sex);
        }

        // introduction
        String introduction = updateUserReqVO.getIntroduction();
        if(StringUtils.isNotBlank(introduction)) {
            Preconditions.checkArgument(ParamUtils.checkLength(introduction, 100), ResponseCodeEnum.INTRODUCTION_VALID_FAIL.getErrorMessage());
            needUpdate = true;
            user.setIntroduction(introduction);
        }

        if(needUpdate) {
            user.setUpdateTime(LocalDateTime.now());
            userMapper.update(user);
            log.info("更新用户信息：{}", user);
        }
        return Response.success();
    }

    /**
     * 用户注册
     * @param registerUserReqDTO
     * @return
     */
    @Override
    public Response<Long> register(RegisterUserReqDTO registerUserReqDTO) {
        String phone = registerUserReqDTO.getPhone();
        UserDO user = userMapper.selectByPhone(phone);
        log.info("==========> 用户注册，手机号：{}", phone);
        if(Objects.nonNull(user)) return Response.success(user.getId());

        String xhsId = distributedIdGeneratorRpcService.getXhsId();
        String userIdStr = distributedIdGeneratorRpcService.getUserId();
        Long userId = Long.valueOf(userIdStr);

        UserDO userDO = UserDO.builder()
                .id(userId)
                .phone(phone)
                .xhsId(xhsId) // 自动生成小红书号 ID
                .nickname("小红薯" + xhsId) // 自动生成昵称, 如：小红薯10000
                .status(StatusEnum.ENABLE.getValue()) // 状态为启用
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(DeletedEnum.NO.isDeleted()) // 逻辑删除
                .build();

        // 添加入库
        userMapper.insert(userDO);

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

        return Response.success(userId);
    }

    /**
     * 根据手机号查询用户信息
     * @param findUserByPhoneReqDTO
     * @return
     */
    @Override
    public Response<FindUserByPhoneRspDTO> findUserByPhone(FindUserByPhoneReqDTO findUserByPhoneReqDTO) {
        String phone = findUserByPhoneReqDTO.getPhone();
        UserDO user = userMapper.selectByPhone(phone);

        if(Objects.isNull(user)) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }

        FindUserByPhoneRspDTO response = FindUserByPhoneRspDTO.builder()
                .id(user.getId())
                .password(user.getPassword())
                .build();
        return Response.success(response);
    }

    @Override
    public Response<?> editPassword(UpdateUserPasswordReqDTO updateUserPasswordReqDTO) {
        String password = updateUserPasswordReqDTO.getEncodedPassword();
        Long userId = LoginUserContextHolder.getLoginUserId();
        UserDO userDO = UserDO.builder()
                .id(userId)
                .password(password)
                .updateTime(LocalDateTime.now())
                .build();
        userMapper.update(userDO);
        return Response.success();
    }

    @Override
    public Response<FindUserByIdRspDTO> findUserById(FindUserByIdReqDTO findUserByIdReqDTO) {
        Long userId = findUserByIdReqDTO.getId();
        UserDO userDO = userMapper.selectById(userId);

        if(Objects.isNull(userDO)) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }

        FindUserByIdRspDTO findUserByIdRspDTO = FindUserByIdRspDTO.builder()
                .id(userId)
                .nickname(userDO.getNickname())
                .avatar(userDO.getAvatar())
                .build();
        return Response.success(findUserByIdRspDTO);
    }
}
