package org.n1vnhil.xhs.note.biz.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopNoteReqVO {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotNull(message = "置顶状态不能为空")
    private Boolean top;

}
