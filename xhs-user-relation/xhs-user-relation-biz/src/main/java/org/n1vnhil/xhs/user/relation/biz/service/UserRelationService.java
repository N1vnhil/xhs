package org.n1vnhil.xhs.user.relation.biz.service;

import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FindFollowingListReqVO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FindFollowingUserRspVO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FollowUserReqVO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.UnfollowUserReqVO;

public interface UserRelationService {

    /**
     * 关注用户
     * @param followUserReqVO
     * @return
     */
    Response<?> follow(FollowUserReqVO followUserReqVO);

    /**
     * 取关用户
     * @param unfollowUserReqVO
     * @return
     */
    Response<?> unfollow(UnfollowUserReqVO unfollowUserReqVO);

    /**
     * 获取用户关注列表
     * @param findFollowingListReqVO
     * @return
     */
    PageResponse<FindFollowingUserRspVO> findFollowingUserList(FindFollowingListReqVO findFollowingListReqVO);

}
