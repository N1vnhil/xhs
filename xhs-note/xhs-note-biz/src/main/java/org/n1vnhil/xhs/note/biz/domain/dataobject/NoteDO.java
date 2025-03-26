package org.n1vnhil.xhs.note.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDO {

    private Long id;

    private String title;

    private Boolean contentEmpty;

    private Long creatorId;

    private Long topicId;

    private String topicName;

    private Boolean top;

    private Integer type;

    private String imgUris;

    private String videoUri;

    private Integer visible;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
