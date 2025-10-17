package com.knight.contexts.serviceprofiles.management.infra;

import io.micronaut.runtime.Micronaut;

/**
 * Test application for integration tests.
 */
public class TestApplication {
    public static void main(String[] args) {
        Micronaut.run(TestApplication.class, args);
    }
}
