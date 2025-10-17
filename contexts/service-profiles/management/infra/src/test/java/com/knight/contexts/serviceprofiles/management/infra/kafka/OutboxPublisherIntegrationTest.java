package com.knight.contexts.serviceprofiles.management.infra.kafka;

import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.OutboxEventEntity;
import com.knight.contexts.serviceprofiles.management.infra.persistence.repository.OutboxEventRepository;
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
 * Integration test for Outbox publisher.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OutboxPublisherIntegrationTest implements TestPropertyProvider {

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
    OutboxEventRepository outboxRepository;

    @Test
    void shouldSaveEventToOutbox() {
        // Given
        OutboxEventEntity event = new OutboxEventEntity(
            UUID.randomUUID(),
            "ServicingProfile",
            "urn:servicing-profile:srf:TEST123",
            "ServicingProfileCreated",
            "{\"profileId\":\"urn:servicing-profile:srf:TEST123\"}",
            UUID.randomUUID()
        );

        // When
        outboxRepository.save(event);

        // Then
        var retrieved = outboxRepository.findById(event.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStatus()).isEqualTo(OutboxEventEntity.OutboxStatus.PENDING);
    }

    @Test
    void shouldCountPendingEvents() {
        // Given
        OutboxEventEntity event1 = new OutboxEventEntity(
            UUID.randomUUID(),
            "ServicingProfile",
            "urn:servicing-profile:srf:TEST456",
            "ServicingProfileCreated",
            "{}",
            UUID.randomUUID()
        );
        outboxRepository.save(event1);

        // When
        long count = outboxRepository.countByStatus(OutboxEventEntity.OutboxStatus.PENDING);

        // Then
        assertThat(count).isGreaterThanOrEqualTo(1);
    }
}
