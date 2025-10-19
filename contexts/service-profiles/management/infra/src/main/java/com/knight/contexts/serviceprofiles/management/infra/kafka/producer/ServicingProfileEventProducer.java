package com.knight.contexts.serviceprofiles.management.infra.kafka.producer;

import com.knight.contexts.serviceprofiles.management.api.events.ServicingProfileCreated;
import com.knight.contexts.serviceprofiles.management.app.service.SpmApplicationService;
import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.OutboxEventEntity;
import com.knight.contexts.serviceprofiles.management.infra.persistence.repository.OutboxEventRepository;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Service for saving domain events to outbox table.
 * Events are published asynchronously by OutboxPublisher.
 */
@Singleton
public class ServicingProfileEventProducer implements SpmApplicationService.ServicingProfileEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ServicingProfileEventProducer.class);

    private final OutboxEventRepository outboxRepository;
    private final JsonMapper jsonMapper;

    public ServicingProfileEventProducer(
        OutboxEventRepository outboxRepository,
        JsonMapper jsonMapper
    ) {
        this.outboxRepository = outboxRepository;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Save ServicingProfileCreated event to outbox.
     * Must be called within same transaction as aggregate save.
     */
    public void publishServicingProfileCreated(ServicingProfileCreated event) {
        try {
            String payload = jsonMapper.writeValueAsString(event);

            OutboxEventEntity outboxEvent = new OutboxEventEntity(
                UUID.randomUUID(),
                "ServicingProfile",
                event.profileId(),
                "ServicingProfileCreated",
                payload,
                UUID.randomUUID() // correlation ID
            );

            outboxRepository.save(outboxEvent);

            LOG.debug("Saved ServicingProfileCreated event to outbox: {}", event.profileId());

        } catch (Exception e) {
            LOG.error("Failed to save event to outbox", e);
            throw new RuntimeException("Failed to save event to outbox", e);
        }
    }
}
