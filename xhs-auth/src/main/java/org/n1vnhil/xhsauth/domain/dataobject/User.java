package org.n1vnhil.xhsauth.domain.dataobject;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @NotBlank(message = "昵称不能为空")
    private String nickName;

    private LocalDateTime createTime;

}
