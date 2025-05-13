package org.n1vnhil.xhs.count.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CountCollectUncollectMqDTO {

    private Long userId;

    private Long noteId;

    /**
     * 0：取消收藏
     * 1：收藏笔记
     */
    private Integer type;

    private LocalDateTime createTime;

    private Long noteCreatorId;

}
