package com.knight.contexts.users.users.infra.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

/**
 * JPA Configuration for Micronaut Data.
 * Ensures proper entity scanning and JPA setup.
 */
@Factory
@Requires(beans = {javax.sql.DataSource.class})
public class JpaConfiguration {
    // Micronaut Data JPA will auto-configure when this factory is present
    // and a DataSource bean exists
}
