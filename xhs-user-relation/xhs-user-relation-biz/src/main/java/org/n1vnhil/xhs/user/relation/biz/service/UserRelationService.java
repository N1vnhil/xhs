package org.n1vnhil.xhs.user.relation.biz.service;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FollowUserReqVO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.UnfollowUserReqVO;

public interface UserRelationService {

    Response<?> follow(FollowUserReqVO followUserReqVO);

    Response<?> unfollow(UnfollowUserReqVO unfollowUserReqVO);

}
