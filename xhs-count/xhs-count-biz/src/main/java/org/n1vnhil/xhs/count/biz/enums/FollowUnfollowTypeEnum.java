package org.n1vnhil.xhs.count.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowUnfollowTypeEnum {

    FOLLOW(1),
    UNFOLLOW(0)
    ;

    private final Integer type;

    public static FollowUnfollowTypeEnum valueOf(Integer type) {
        for(FollowUnfollowTypeEnum followUnfollowTypeEnum: FollowUnfollowTypeEnum.values()) {
            if(followUnfollowTypeEnum.getType().equals(type)) return followUnfollowTypeEnum;
        }
        return null;
    }

}
