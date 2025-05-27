package org.n1vnhil.xhs.search.model.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchNoteReqVO {

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo;

    @NotBlank(message = "关键词不能为空")
    private String keyword;

}
