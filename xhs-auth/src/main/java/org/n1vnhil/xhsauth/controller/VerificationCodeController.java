package org.n1vnhil.xhsauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhsauth.model.vo.verificationcode.SendVerificationCodeReqCodeVO;
import org.n1vnhil.xhsauth.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/verification")
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @PostMapping("/sendCode")
    @ApiOperationLog(description = "发送短信验证码")
    public Response<?> sendVerificationCode(@RequestBody @Validated SendVerificationCodeReqCodeVO sendVerificationCodeReqCodeVO) {
        log.info("发送验证码：{}", sendVerificationCodeReqCodeVO);
        verificationCodeService.sendVerificationCode(sendVerificationCodeReqCodeVO);
        return null;
    }

}
