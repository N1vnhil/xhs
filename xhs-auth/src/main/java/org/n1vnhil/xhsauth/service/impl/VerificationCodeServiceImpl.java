package org.n1vnhil.xhsauth.service.impl;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.constant.RedisKeyConstants;
import org.n1vnhil.xhsauth.enums.ResponseCodeEnum;
import org.n1vnhil.xhsauth.model.vo.verificationcode.SendVerificationCodeRequestCodeVO;
import org.n1vnhil.xhsauth.service.VerificationCodeService;
import org.n1vnhil.xhsauth.sms.AliyunAccessKeyProperties;
import org.n1vnhil.xhsauth.sms.AliyunSmsSendHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private AliyunSmsSendHelper aliyunSmsSendHelper;

    @Autowired
    private AliyunAccessKeyProperties aliyunAccessKeyProperties;

    public Response<?> sendVerificationCode(SendVerificationCodeRequestCodeVO sendVerificationCodeRequestCodeVO) {
        // 检查缓存情况
        String phone = sendVerificationCodeRequestCodeVO.getPhone();
        String redisKey = RedisKeyConstants.buildVerificationCode(phone);
        boolean sent = redisTemplate.hasKey(redisKey);
        if(sent) {
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_SEND_FREQUENTLY);
        }

        // 缓存验证码三分钟，同时发送验证码
        String code = RandomUtil.randomNumbers(6);

        taskExecutor.submit(() -> {
                    String signName = aliyunAccessKeyProperties.getSignName();
                    String templateCode = aliyunAccessKeyProperties.getTemplateCode();
                    String templateParam = String.format("{\"code\":\"%s\"}", code);
                    aliyunSmsSendHelper.sendMessage(signName, templateCode, phone, templateParam);
        });
        log.info("手机号：{}，验证码已发送【{}】", phone, code);


        redisTemplate.opsForValue().set(redisKey, code, 3, TimeUnit.MINUTES);
        return Response.success();
    }

}
