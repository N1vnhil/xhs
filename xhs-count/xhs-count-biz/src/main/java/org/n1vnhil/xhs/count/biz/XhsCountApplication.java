package org.n1vnhil.xhs.count.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.n1vnhil.xhs.count.biz.domain.mapper")
public class XhsCountApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsCountApplication.class, args);
    }

}
