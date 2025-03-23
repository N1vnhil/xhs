package org.n1vnhil.xhs.user.biz.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserReqVO {

    private MultipartFile avatar;

    private String nickname;

    private String xhsId;

    private LocalDate birthday;

    private MultipartFile backgroundImg;

    private Integer sex;

    private String introduction;

}
