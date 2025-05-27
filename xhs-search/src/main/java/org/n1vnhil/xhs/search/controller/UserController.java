package org.n1vnhil.xhs.search.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.xhs.search.model.vo.SearchUserReqVO;
import org.n1vnhil.xhs.search.model.vo.SearchUserRspVO;
import org.n1vnhil.xhs.search.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/search")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user")
    @ApiOperationLog(description = "搜索用户")
    public PageResponse<SearchUserRspVO> searchUser(SearchUserReqVO searchUserReqVO) {
        return userService.searchUserByKeywordAndPage(searchUserReqVO);
    }

}
