package org.n1vnhil.xhs.user.relation.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowingDO {

    Long id;

    Long userId;

    Long followingUserId;

    LocalDateTime createTime;

}
