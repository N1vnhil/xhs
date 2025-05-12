package org.n1vnhil.xhs.note.biz.model.vo;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectNoteReqVO {

    @NotNull(message = "笔记id不能为空")
    private Long id;


}
