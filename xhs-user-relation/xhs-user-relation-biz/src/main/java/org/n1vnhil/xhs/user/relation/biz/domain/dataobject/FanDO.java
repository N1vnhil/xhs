package org.n1vnhil.xhs.user.relation.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FanDO {

    private Long id;

    private Long userId;

    private Long fansUserId;

    private LocalDateTime createTime;

}
