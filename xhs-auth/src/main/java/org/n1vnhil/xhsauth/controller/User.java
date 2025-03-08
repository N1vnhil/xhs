package org.n1vnhil.xhsauth.controller;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class User {

    private String nickname;

    private LocalDateTime createTime;

}
