package org.n1vnhil.xhs.user.biz.service.impl;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.common.util.ParamUtils;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;
import org.n1vnhil.xhs.oss.api.FileFeignApi;
import org.n1vnhil.xhs.user.biz.domain.dataobject.UserDO;
import org.n1vnhil.xhs.user.biz.domain.mapper.UserMapper;
import org.n1vnhil.xhs.user.biz.enums.GenderEnum;
import org.n1vnhil.xhs.user.biz.enums.ResponseCodeEnum;
import org.n1vnhil.xhs.user.biz.model.vo.UpdateUserReqVO;
import org.n1vnhil.xhs.user.biz.rpc.OssRpcService;
import org.n1vnhil.xhs.user.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OssRpcService ossRpcService;

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

}
