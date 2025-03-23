package org.n1vnhil.xhs.user.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("org.n1vnhil.xhs.user.biz.domain.mapper")
@SpringBootApplication
@EnableFeignClients(basePackages = "org.n1vnhil.xhs")
public class XhsUserBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsUserBizApplication.class, args);
    }

}
