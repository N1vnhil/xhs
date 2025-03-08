package org.n1vnhil.xhsauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.n1vnhil.xhsauth.domain.mapper")
@SpringBootApplication
public class XhsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsAuthApplication.class, args);
    }

}
