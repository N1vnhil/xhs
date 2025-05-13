package org.n1vnhil.xhs.note.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NoteOperateMqDTO {

    private Long creatorId;

    private Long noteId;

    /**
     * 0：笔记删除
     * 1：笔记发布
     */
    private Integer type;

}
