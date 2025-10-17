package com.knight.contexts.users.users.infra.persistence;

import com.knight.contexts.users.users.app.service.UserApplicationService;
import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.UserId;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for User repository with Testcontainers.
 */
@MicronautTest(
    transactional = false,
    packages = "com.knight.contexts.users.users"
)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryIntegrationTest implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test");

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword()
        );
    }

    @Inject
    UserApplicationService.UserRepository repository;

    @Test
    void shouldSaveAndRetrieveUser() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID().toString());
        ClientId clientId = ClientId.of("srf:CAN123456");
        User user = User.create(
            userId,
            "test@example.com",
            User.UserType.INDIRECT,
            User.IdentityProvider.OKTA,
            clientId
        );

        // When
        repository.save(user);
        var retrieved = repository.findById(userId);

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrieved.get().getStatus()).isEqualTo(User.Status.PENDING);
        assertThat(retrieved.get().getUserType()).isEqualTo(User.UserType.INDIRECT);
        assertThat(retrieved.get().getIdentityProvider()).isEqualTo(User.IdentityProvider.OKTA);
    }

    @Test
    void shouldUpdateExistingUser() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID().toString());
        ClientId clientId = ClientId.of("srf:CAN789012");
        User user = User.create(
            userId,
            "update@example.com",
            User.UserType.DIRECT,
            User.IdentityProvider.A_AND_P,
            clientId
        );
        repository.save(user);

        // When
        user.activate();
        repository.save(user);

        // Then
        var retrieved = repository.findById(userId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStatus()).isEqualTo(User.Status.ACTIVE);
    }

    @Test
    void shouldHandleUserLocking() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID().toString());
        ClientId clientId = ClientId.of("srf:CAN111222");
        User user = User.create(
            userId,
            "lock@example.com",
            User.UserType.INDIRECT,
            User.IdentityProvider.OKTA,
            clientId
        );
        user.activate();
        repository.save(user);

        // When
        user.lock("Security violation");
        repository.save(user);

        // Then
        var retrieved = repository.findById(userId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStatus()).isEqualTo(User.Status.LOCKED);
        assertThat(retrieved.get().getLockReason()).isEqualTo("Security violation");
    }

    @Test
    void shouldHandleUserDeactivation() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID().toString());
        ClientId clientId = ClientId.of("srf:CAN333444");
        User user = User.create(
            userId,
            "deactivate@example.com",
            User.UserType.INDIRECT,
            User.IdentityProvider.OKTA,
            clientId
        );
        user.activate();
        repository.save(user);

        // When
        user.deactivate("Account closed");
        repository.save(user);

        // Then
        var retrieved = repository.findById(userId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStatus()).isEqualTo(User.Status.DEACTIVATED);
        assertThat(retrieved.get().getDeactivationReason()).isEqualTo("Account closed");
    }
}
