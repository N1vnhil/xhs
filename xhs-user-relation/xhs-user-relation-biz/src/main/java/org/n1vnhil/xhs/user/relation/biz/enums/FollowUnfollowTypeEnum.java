package org.n1vnhil.xhs.user.relation.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowUnfollowTypeEnum {

    // 关注
    FOLLOW(1),
    // 取关
    UNFOLLOW(2),
    ;

    private final Integer code;

}
