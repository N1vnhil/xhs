package org.n1vnhil.xhs.user.relation.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "org.n1vnhil.xhs")
@SpringBootApplication
public class XhsUserRelationApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsUserRelationApplication.class, args);
    }

}
