package com.knight.contexts.serviceprofiles.management.infra.persistence;

import com.knight.contexts.serviceprofiles.management.app.service.SpmApplicationService;
import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for ServicingProfile repository with Testcontainers.
 */
@MicronautTest(
    transactional = false,
    packages = "com.knight.contexts.serviceprofiles.management"
)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServicingProfileRepositoryIntegrationTest implements TestPropertyProvider {

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
    SpmApplicationService.SpmRepository repository;

    @Test
    void shouldSaveAndRetrieveProfile() {
        // Given
        ClientId clientId = ClientId.of("srf:CAN123456");
        ServicingProfileId profileId = ServicingProfileId.of(clientId);
        ServicingProfile profile = ServicingProfile.create(profileId, clientId, "test@example.com");

        // When
        repository.save(profile);
        var retrieved = repository.findById(profileId);

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getClientId()).isEqualTo(clientId);
        assertThat(retrieved.get().getStatus()).isEqualTo(ServicingProfile.Status.PENDING);
    }

    @Test
    void shouldUpdateExistingProfile() {
        // Given
        ClientId clientId = ClientId.of("srf:CAN789012");
        ServicingProfileId profileId = ServicingProfileId.of(clientId);
        ServicingProfile profile = ServicingProfile.create(profileId, clientId, "test@example.com");
        repository.save(profile);

        // When
        profile.enrollService("RECEIVABLES", "{}");
        repository.save(profile);

        // Then
        var retrieved = repository.findById(profileId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getServiceEnrollments()).hasSize(1);
        assertThat(retrieved.get().getStatus()).isEqualTo(ServicingProfile.Status.ACTIVE);
    }
}
