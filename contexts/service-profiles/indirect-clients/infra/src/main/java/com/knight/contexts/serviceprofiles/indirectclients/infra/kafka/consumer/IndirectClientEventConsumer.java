package com.knight.contexts.serviceprofiles.indirectclients.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.InboxEventEntity;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.repository.InboxEventRepository;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Kafka consumer with Inbox pattern for idempotent event processing.
 * Example consumer - adapt based on actual events to consume.
 */
@Singleton
@Requires(property = "kafka.enabled", value = "true")
public class IndirectClientEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(IndirectClientEventConsumer.class);

    private final InboxEventRepository inboxRepository;
    private final ObjectMapper objectMapper;

    public IndirectClientEventConsumer(
        InboxEventRepository inboxRepository,
        ObjectMapper objectMapper
    ) {
        this.inboxRepository = inboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Example: Consume events from other bounded contexts.
     * Adjust topic and event type as needed.
     */
    @KafkaListener(
        groupId = "indirect-client-management",
        offsetReset = OffsetReset.EARLIEST
    )
    @Topic("users.users.user-created")
    @Transactional
    public void onUserCreated(String eventPayload) {
        try {
            // Parse event
            var eventNode = objectMapper.readTree(eventPayload);
            UUID eventId = UUID.fromString(eventNode.get("eventId").asText());
            String eventType = eventNode.get("eventType").asText();

            // Check if already processed (idempotency)
            if (inboxRepository.existsByEventId(eventId)) {
                LOG.debug("Event {} already processed, skipping", eventId);
                return;
            }

            // Save to inbox (deduplication)
            InboxEventEntity inboxEvent = new InboxEventEntity(
                eventId,
                eventType,
                eventPayload
            );
            inboxRepository.save(inboxEvent);

            // Process event (business logic)
            processUserCreatedEvent(eventNode);

            // Mark as processed
            inboxEvent.markProcessed();
            inboxRepository.update(inboxEvent);

            LOG.info("Successfully processed event {}", eventId);

        } catch (Exception e) {
            LOG.error("Failed to process event", e);
            throw new RuntimeException("Event processing failed", e);
        }
    }

    private void processUserCreatedEvent(com.fasterxml.jackson.databind.JsonNode eventNode) {
        // Implement business logic here
        // Example: Create internal reference, trigger workflow, etc.
        LOG.info("Processing user created event: {}", eventNode.get("payload"));
    }
}
