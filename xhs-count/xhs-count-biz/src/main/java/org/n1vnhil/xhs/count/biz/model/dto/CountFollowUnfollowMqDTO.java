package org.n1vnhil.xhs.count.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CountFollowUnfollowMqDTO {

    private Long userId;

    private Long targetId;

    /**
     * 1：关注
     * 0：取关
     */
    private Integer type;

}


