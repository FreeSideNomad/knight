package com.knight.contexts.approvalworkflows.engine.infra.kafka.consumer;

import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.InboxEventEntity;
import com.knight.contexts.approvalworkflows.engine.infra.persistence.repository.InboxEventRepository;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Consumer for ApprovalWorkflow domain events from Kafka.
 * Implements inbox pattern for idempotent event processing.
 */
@Singleton
@KafkaListener(groupId = "approval-workflow-engine")
@Requires(property = "kafka.enabled", value = "true")
public class ApprovalWorkflowEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalWorkflowEventConsumer.class);

    private final InboxEventRepository inboxRepository;

    public ApprovalWorkflowEventConsumer(InboxEventRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    /**
     * Consume workflow-initiated events.
     */
    @Topic("approvals.workflow-initiated")
    @Transactional
    public void onWorkflowInitiated(String payload) {
        UUID eventId = extractEventId(payload);

        // Check inbox for duplicate
        if (inboxRepository.existsByEventId(eventId)) {
            LOG.debug("Event {} already processed, skipping", eventId);
            return;
        }

        try {
            // Save to inbox first
            var inboxEvent = new InboxEventEntity(eventId, "WorkflowInitiated", payload);
            inboxRepository.save(inboxEvent);

            // Process event (business logic here)
            LOG.info("Processing WorkflowInitiated event: {}", eventId);

            // Mark as processed
            inboxEvent.markProcessed();
            inboxRepository.update(inboxEvent);

        } catch (Exception e) {
            LOG.error("Failed to process event {}: {}", eventId, e.getMessage(), e);
            // Event will remain in PENDING state and can be retried
        }
    }

    /**
     * Consume workflow-approved events.
     */
    @Topic("approvals.workflow-approved")
    @Transactional
    public void onWorkflowApproved(String payload) {
        UUID eventId = extractEventId(payload);

        // Check inbox for duplicate
        if (inboxRepository.existsByEventId(eventId)) {
            LOG.debug("Event {} already processed, skipping", eventId);
            return;
        }

        try {
            // Save to inbox first
            var inboxEvent = new InboxEventEntity(eventId, "WorkflowApproved", payload);
            inboxRepository.save(inboxEvent);

            // Process event (business logic here)
            LOG.info("Processing WorkflowApproved event: {}", eventId);

            // Mark as processed
            inboxEvent.markProcessed();
            inboxRepository.update(inboxEvent);

        } catch (Exception e) {
            LOG.error("Failed to process event {}: {}", eventId, e.getMessage(), e);
            // Event will remain in PENDING state and can be retried
        }
    }

    /**
     * Consume workflow-rejected events.
     */
    @Topic("approvals.workflow-rejected")
    @Transactional
    public void onWorkflowRejected(String payload) {
        UUID eventId = extractEventId(payload);

        // Check inbox for duplicate
        if (inboxRepository.existsByEventId(eventId)) {
            LOG.debug("Event {} already processed, skipping", eventId);
            return;
        }

        try {
            // Save to inbox first
            var inboxEvent = new InboxEventEntity(eventId, "WorkflowRejected", payload);
            inboxRepository.save(inboxEvent);

            // Process event (business logic here)
            LOG.info("Processing WorkflowRejected event: {}", eventId);

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
