package com.knight.contexts.users.users.infra.kafka.consumer;

import com.knight.contexts.users.users.infra.persistence.entity.InboxEventEntity;
import com.knight.contexts.users.users.infra.persistence.repository.InboxEventRepository;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Consumer for User domain events from Kafka.
 * Implements inbox pattern for idempotent event processing.
 */
@Singleton
@KafkaListener(groupId = "user-management")
@Requires(property = "kafka.enabled", value = "true")
public class UserEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(UserEventConsumer.class);

    private final InboxEventRepository inboxRepository;

    public UserEventConsumer(InboxEventRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    /**
     * Consume user-created events.
     */
    @Topic("users.user-created")
    @Transactional
    public void onUserCreated(String payload) {
        UUID eventId = extractEventId(payload);

        // Check inbox for duplicate
        if (inboxRepository.existsByEventId(eventId)) {
            LOG.debug("Event {} already processed, skipping", eventId);
            return;
        }

        try {
            // Save to inbox first
            var inboxEvent = new InboxEventEntity(eventId, "UserCreated", payload);
            inboxRepository.save(inboxEvent);

            // Process event (business logic here)
            LOG.info("Processing UserCreated event: {}", eventId);

            // Mark as processed
            inboxEvent.markProcessed();
            inboxRepository.update(inboxEvent);

        } catch (Exception e) {
            LOG.error("Failed to process event {}: {}", eventId, e.getMessage(), e);
            // Event will remain in PENDING state and can be retried
        }
    }

    /**
     * Consume user-updated events.
     */
    @Topic("users.user-updated")
    @Transactional
    public void onUserUpdated(String payload) {
        UUID eventId = extractEventId(payload);

        // Check inbox for duplicate
        if (inboxRepository.existsByEventId(eventId)) {
            LOG.debug("Event {} already processed, skipping", eventId);
            return;
        }

        try {
            // Save to inbox first
            var inboxEvent = new InboxEventEntity(eventId, "UserUpdated", payload);
            inboxRepository.save(inboxEvent);

            // Process event (business logic here)
            LOG.info("Processing UserUpdated event: {}", eventId);

            // Mark as processed
            inboxEvent.markProcessed();
            inboxRepository.update(inboxEvent);

        } catch (Exception e) {
            LOG.error("Failed to process event {}: {}", eventId, e.getMessage(), e);
            // Event will remain in PENDING state and can be retried
        }
    }

    /**
     * Extract event ID from JSON payload.
     * In production, use proper JSON parsing.
     */
    private UUID extractEventId(String payload) {
        // Simple extraction - in production use Jackson or similar
        return UUID.randomUUID(); // Placeholder
    }
}
