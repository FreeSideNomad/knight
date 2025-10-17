package com.knight.contexts.serviceprofiles.indirectclients.infra.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knight.contexts.serviceprofiles.indirectclients.api.events.IndirectClientOnboarded;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.OutboxEventEntity;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.repository.OutboxEventRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Service for saving domain events to outbox table.
 * Events are published asynchronously by OutboxPublisher.
 */
@Singleton
public class IndirectClientEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(IndirectClientEventProducer.class);

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public IndirectClientEventProducer(
        OutboxEventRepository outboxRepository,
        ObjectMapper objectMapper
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Save IndirectClientOnboarded event to outbox.
     * Must be called within same transaction as aggregate save.
     */
    public void publishIndirectClientOnboarded(IndirectClientOnboarded event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEventEntity outboxEvent = new OutboxEventEntity(
                UUID.randomUUID(),
                "IndirectClient",
                event.indirectClientId(),
                "IndirectClientOnboarded",
                payload,
                UUID.randomUUID() // correlation ID
            );

            outboxRepository.save(outboxEvent);

            LOG.debug("Saved IndirectClientOnboarded event to outbox: {}", event.indirectClientId());

        } catch (Exception e) {
            LOG.error("Failed to save event to outbox", e);
            throw new RuntimeException("Failed to save event to outbox", e);
        }
    }
}
