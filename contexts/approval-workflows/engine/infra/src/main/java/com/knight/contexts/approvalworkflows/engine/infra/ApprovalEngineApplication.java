package com.knight.contexts.approvalworkflows.engine.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot application for Approval Engine bounded context.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.knight.contexts.approvalworkflows.engine"
})
public class ApprovalEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApprovalEngineApplication.class, args);
    }
}
