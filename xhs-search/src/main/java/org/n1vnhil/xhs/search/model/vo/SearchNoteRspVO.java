package org.n1vnhil.xhs.search.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchNoteRspVO {

    private Long noteId;

    private String cover;

    private String title;

    private  String highlightTitle;

    private String avatar;

    private String nickname;

    private LocalDateTime updateTime;

    private String likeTotal;


}
