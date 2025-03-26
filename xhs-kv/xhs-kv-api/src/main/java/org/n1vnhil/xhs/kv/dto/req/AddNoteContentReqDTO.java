package org.n1vnhil.xhs.kv.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddNoteContentReqDTO {

    @NotBlank(message = "笔记id不能为空")
    private String uuid;

    @NotNull(message = "笔记内容不能为空")
    private String content;

}
