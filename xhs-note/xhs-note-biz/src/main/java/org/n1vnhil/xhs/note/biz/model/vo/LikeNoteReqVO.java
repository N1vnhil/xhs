package org.n1vnhil.xhs.note.biz.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeNoteReqVO {

    @NotNull(message = "笔记id不能为空")
    private Long id;

}
