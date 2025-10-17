package com.knight.contexts.users.users.infra.kafka.producer;

import com.knight.contexts.users.users.infra.persistence.entity.OutboxEventEntity;
import com.knight.contexts.users.users.infra.persistence.repository.OutboxEventRepository;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Producer for User domain events.
 * Saves events to outbox table for reliable publishing.
 */
@Singleton
@Requires(property = "kafka.enabled", value = "true")
public class UserEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(UserEventProducer.class);
    private static final String AGGREGATE_TYPE = "User";

    private final OutboxEventRepository outboxRepository;

    public UserEventProducer(OutboxEventRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    /**
     * Publish event by saving to outbox.
     * OutboxPublisher will pick it up and send to Kafka.
     */
    @Transactional
    public void publishEvent(String aggregateId, String eventType, String payload) {
        LOG.debug("Publishing event: {} for aggregate: {}", eventType, aggregateId);

        var event = new OutboxEventEntity(
            UUID.randomUUID(),
            AGGREGATE_TYPE,
            aggregateId,
            eventType,
            payload,
            UUID.randomUUID() // correlation ID
        );

        outboxRepository.save(event);

        LOG.info("Event saved to outbox: {} for aggregate: {}", eventType, aggregateId);
    }
}
