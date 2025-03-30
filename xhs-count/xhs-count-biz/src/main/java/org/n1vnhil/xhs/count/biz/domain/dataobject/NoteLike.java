package org.n1vnhil.xhs.count.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteLike {

    private Long id;

    private Long userId;

    private Long noteId;

    private LocalDateTime createTime;

    private Integer status;

}
