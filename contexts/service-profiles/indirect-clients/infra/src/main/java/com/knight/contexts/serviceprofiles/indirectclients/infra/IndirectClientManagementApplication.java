package com.knight.contexts.serviceprofiles.indirectclients.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot application for Indirect Client Management bounded context.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.knight.contexts.serviceprofiles.indirectclients"
})
public class IndirectClientManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndirectClientManagementApplication.class, args);
    }
}
