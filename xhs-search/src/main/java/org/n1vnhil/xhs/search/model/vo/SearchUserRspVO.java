package org.n1vnhil.xhs.search.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRspVO {

    private Long userId;

    private String nickname;

    private String avatar;

    private String xhsId;

    private Integer noteTotal;

    private Integer fansTotal;

    private String highlightNickname;

}
