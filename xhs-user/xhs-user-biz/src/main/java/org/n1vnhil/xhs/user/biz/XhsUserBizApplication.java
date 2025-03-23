package org.n1vnhil.xhs.user.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.n1vnhil.xhs.user.biz.domain.mapper")
@SpringBootApplication
public class XhsUserBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsUserBizApplication.class, args);
    }

}
