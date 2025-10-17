package com.knight.bff.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Backend-for-Frontend (BFF) Web Application
 *
 * Provides composition endpoints for employee portal and self-service portal.
 * Orchestrates queries across multiple bounded contexts to minimize client round-trips.
 *
 * Port: 8080
 */
@SpringBootApplication
public class BffWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BffWebApplication.class, args);
    }
}
