package com.knight.contexts.users.users.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot application for User Management bounded context.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.knight.contexts.users.users"
})
public class UserManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
    }
}
