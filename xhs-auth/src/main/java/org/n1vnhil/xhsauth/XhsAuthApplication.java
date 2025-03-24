package org.n1vnhil.xhsauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients(basePackages = "org.n1vnhil.xhs")
public class XhsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsAuthApplication.class, args);
    }

}
