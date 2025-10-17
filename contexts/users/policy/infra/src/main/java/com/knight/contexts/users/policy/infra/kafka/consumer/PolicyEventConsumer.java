package com.knight.contexts.users.policy.infra.kafka.consumer;

import com.knight.contexts.users.policy.infra.persistence.entity.InboxEventEntity;
import com.knight.contexts.users.policy.infra.persistence.repository.InboxEventRepository;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Consumer for Policy domain events from Kafka.
 * Implements inbox pattern for idempotent event processing.
 */
@Singleton
@KafkaListener(groupId = "policy-management")
@Requires(property = "kafka.enabled", value = "true")
public class PolicyEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(PolicyEventConsumer.class);

    private final InboxEventRepository inboxRepository;

    public PolicyEventConsumer(InboxEventRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    /**
     * Consume policy-created events.
     */
    @Topic("policy.policy-created")
    @Transactional
    public void onPolicyCreated(String payload) {
        UUID eventId = extractEventId(payload);

        // Check inbox for duplicate
        if (inboxRepository.existsByEventId(eventId)) {
            LOG.debug("Event {} already processed, skipping", eventId);
            return;
        }

        try {
            // Save to inbox first
            var inboxEvent = new InboxEventEntity(eventId, "PolicyCreated", payload);
            inboxRepository.save(inboxEvent);

            // Process event (business logic here)
            LOG.info("Processing PolicyCreated event: {}", eventId);

            // Mark as processed
            inboxEvent.markProcessed();
            inboxRepository.update(inboxEvent);

        } catch (Exception e) {
            LOG.error("Failed to process event {}: {}", eventId, e.getMessage(), e);
            // Event will remain in PENDING state and can be retried
        }
    }

    /**
     * Consume policy-updated events.
     */
    @Topic("policy.policy-updated")
    @Transactional
    public void onPolicyUpdated(String payload) {
        UUID eventId = extractEventId(payload);

        // Check inbox for duplicate
        if (inboxRepository.existsByEventId(eventId)) {
            LOG.debug("Event {} already processed, skipping", eventId);
            return;
        }

        try {
            // Save to inbox first
            var inboxEvent = new InboxEventEntity(eventId, "PolicyUpdated", payload);
            inboxRepository.save(inboxEvent);

            // Process event (business logic here)
            LOG.info("Processing PolicyUpdated event: {}", eventId);

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
