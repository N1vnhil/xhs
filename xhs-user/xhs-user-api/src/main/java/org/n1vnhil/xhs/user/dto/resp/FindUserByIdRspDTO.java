package org.n1vnhil.xhs.user.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindUserByIdRspDTO {

    private Long id;

    private String nickname;

    private String avatar;

    private String introduction;

}
