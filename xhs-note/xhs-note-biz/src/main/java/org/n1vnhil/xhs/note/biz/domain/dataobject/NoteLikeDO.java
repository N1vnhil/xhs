package org.n1vnhil.xhs.note.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteLikeDO {

    Long id;

    Long userId;

    Long noteId;

    LocalDateTime createTime;

    Integer status;

}
