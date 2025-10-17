package com.knight.contexts.users.policy.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot application for Policy bounded context.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.knight.contexts.users.policy"
})
public class PolicyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolicyApplication.class, args);
    }
}
