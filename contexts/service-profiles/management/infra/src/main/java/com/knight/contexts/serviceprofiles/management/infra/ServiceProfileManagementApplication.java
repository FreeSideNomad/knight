package com.knight.contexts.serviceprofiles.management.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot application for Service Profile Management bounded context.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.knight.contexts.serviceprofiles.management"
})
public class ServiceProfileManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProfileManagementApplication.class, args);
    }
}
