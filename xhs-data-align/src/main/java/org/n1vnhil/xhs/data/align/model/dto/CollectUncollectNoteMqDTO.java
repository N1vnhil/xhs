package org.n1vnhil.xhs.data.align.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectUncollectNoteMqDTO {

    private Long userId;

    private Long noteId;

    private Integer type;

    private LocalDateTime createTime;

    private Long noteCreatorId;

}
