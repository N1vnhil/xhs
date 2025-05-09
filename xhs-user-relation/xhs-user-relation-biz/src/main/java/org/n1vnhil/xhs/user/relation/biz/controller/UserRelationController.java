package org.n1vnhil.xhs.user.relation.biz.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.relation.biz.model.vo.*;
import org.n1vnhil.xhs.user.relation.biz.service.UserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/relation")
public class UserRelationController {

    @Autowired
    private UserRelationService userRelationService;

    @PostMapping("/follow")
    @ApiOperationLog(description = "关注用户")
    public Response<?> follow(@Validated @RequestBody FollowUserReqVO followUserReqVO) {
        return userRelationService.follow(followUserReqVO);
    }

    @PostMapping("/unfollow")
    @ApiOperationLog(description = "取关用户")
    public Response<?> unfollow(@Validated @RequestBody UnfollowUserReqVO unfollowUserReqVO) {
        return userRelationService.unfollow(unfollowUserReqVO);
    }

    @PostMapping("/following/list")
    @ApiOperationLog(description = "查询关注列表")
    public PageResponse<FindFollowingUserRspVO> findFollowingUsers(@Validated @RequestBody FindFollowingListReqVO findFollowingListReqVO)  {
        return userRelationService.findFollowingUserList(findFollowingListReqVO);
    }

    @PostMapping("/fans/list")
    @ApiOperationLog(description = "查询粉丝列表")
    public PageResponse<FindFansUserRspVO> findFansUser(@Validated @RequestBody FindFansListReqVO findFansListReqVO) {
        return userRelationService.findFansUserList(findFansListReqVO);
    }

}
