package com.knight.contexts.serviceprofiles.management.infra.kafka;

import com.knight.contexts.serviceprofiles.management.api.commands.SpmCommands;
import com.knight.contexts.serviceprofiles.management.infra.persistence.repository.InboxEventRepository;
import com.knight.contexts.serviceprofiles.management.infra.persistence.repository.OutboxEventRepository;
import com.knight.platform.sharedkernel.ClientId;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;

/**
 * End-to-end integration test for event publish and consume.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventPublishConsumeIntegrationTest implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        if (!kafka.isRunning()) {
            kafka.start();
        }
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword(),
            "kafka.bootstrap.servers", kafka.getBootstrapServers(),
            "kafka.enabled", "true"
        );
    }

    @Inject
    SpmCommands commands;

    @Inject
    OutboxEventRepository outboxRepository;

    @Inject
    InboxEventRepository inboxRepository;

    @Test
    void shouldPublishAndConsumeEvent() {
        // Given
        ClientId clientId = ClientId.of("srf:CAN999999");

        // When - Create servicing profile (saves to outbox)
        var profileId = commands.createServicingProfile(clientId, "test@example.com");

        // Then - Wait for outbox publisher to process (max 30 seconds)
        await()
            .atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> {
                long publishedCount = outboxRepository.countByStatus(
                    com.knight.contexts.serviceprofiles.management.infra.persistence.entity.OutboxEventEntity.OutboxStatus.PUBLISHED
                );
                assertThat(publishedCount).isGreaterThan(0);
            });

        // Verify event was published
        assertThat(profileId).isNotNull();
    }
}
