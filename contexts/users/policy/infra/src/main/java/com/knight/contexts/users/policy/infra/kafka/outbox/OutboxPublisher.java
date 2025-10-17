package com.knight.contexts.users.policy.infra.kafka.outbox;

import com.knight.contexts.users.policy.infra.kafka.config.KafkaProducerConfig;
import com.knight.contexts.users.policy.infra.persistence.entity.OutboxEventEntity;
import com.knight.contexts.users.policy.infra.persistence.repository.OutboxEventRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.data.model.Pageable;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled task that polls outbox table and publishes events to Kafka.
 * Runs every 5 seconds.
 */
@Singleton
@Requires(property = "kafka.enabled", value = "true")
public class OutboxPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(OutboxPublisher.class);
    private static final int BATCH_SIZE = 100;
    private static final int MAX_RETRIES = 5;
    private static final int PUBLISH_TIMEOUT_SECONDS = 10;

    private final OutboxEventRepository outboxRepository;
    private final KafkaProducer<String, String> kafkaProducer;
    private final String topicPrefix;

    public OutboxPublisher(
        OutboxEventRepository outboxRepository,
        KafkaProducerConfig producerConfig
    ) {
        this.outboxRepository = outboxRepository;
        this.topicPrefix = "policy";

        // Initialize Kafka Producer
        Properties props = new Properties();
        props.put("bootstrap.servers", producerConfig.getBootstrapServers());
        props.put("key.serializer", producerConfig.getKeySerializer());
        props.put("value.serializer", producerConfig.getValueSerializer());
        props.put("acks", producerConfig.getAcks());
        props.put("retries", producerConfig.getRetries());
        props.put("max.in.flight.requests.per.connection", producerConfig.getMaxInFlightRequestsPerConnection());
        props.put("enable.idempotence", producerConfig.isEnableIdempotence());

        this.kafkaProducer = new KafkaProducer<>(props);
    }

    /**
     * Poll outbox table every 5 seconds and publish pending events.
     */
    @Scheduled(fixedDelay = "5s", initialDelay = "10s")
    @Transactional
    public void publishPendingEvents() {
        try {
            var page = outboxRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEventEntity.OutboxStatus.PENDING,
                Pageable.from(0, BATCH_SIZE)
            );

            var events = page.getContent();
            if (events.isEmpty()) {
                return;
            }

            LOG.info("Publishing {} pending outbox events", events.size());

            for (var event : events) {
                publishEvent(event);
            }

        } catch (Exception e) {
            LOG.error("Error in outbox publisher", e);
        }
    }

    private void publishEvent(OutboxEventEntity event) {
        try {
            // Build topic name: policy.policy-created
            String topic = topicPrefix + "." + toKebabCase(event.getEventType());

            // Build Kafka record
            ProducerRecord<String, String> record = new ProducerRecord<>(
                topic,
                event.getAggregateId(), // Key for partitioning
                event.getPayload()       // JSON payload
            );

            // Send and wait (blocking with timeout)
            kafkaProducer.send(record).get(PUBLISH_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            // Mark as published
            event.markPublished();
            outboxRepository.update(event);

            LOG.debug("Published event {} to topic {}", event.getId(), topic);

        } catch (Exception e) {
            LOG.error("Failed to publish event {}: {}", event.getId(), e.getMessage());

            event.incrementRetryCount();

            if (event.getRetryCount() >= MAX_RETRIES) {
                event.markFailed(e.getMessage());
                LOG.error("Event {} failed after {} retries", event.getId(), MAX_RETRIES);
            }

            outboxRepository.update(event);
        }
    }

    private String toKebabCase(String eventType) {
        // Convert "PolicyCreated" to "policy-created"
        return eventType
            .replaceAll("([a-z])([A-Z])", "$1-$2")
            .toLowerCase();
    }
}
