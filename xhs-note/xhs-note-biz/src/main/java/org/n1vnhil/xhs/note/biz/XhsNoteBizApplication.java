package org.n1vnhil.xhs.note.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.n1vnhil.xhs")
@MapperScan("org.n1vnhil.xhs.note.biz.domain.mapper")
public class XhsNoteBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsNoteBizApplication.class, args);
    }

}
