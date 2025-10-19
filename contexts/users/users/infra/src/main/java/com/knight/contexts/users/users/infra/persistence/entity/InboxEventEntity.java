package com.knight.contexts.users.users.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Inbox pattern entity for idempotent event consumption.
 * Ensures duplicate events are not processed twice.
 */
@Entity
@Table(name = "inbox", schema = "users")
public class InboxEventEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "JSONB")
    private String payload;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InboxStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    public enum InboxStatus {
        PENDING, PROCESSED, FAILED
    }

    // Default constructor for JPA
    public InboxEventEntity() {}

    public InboxEventEntity(
        UUID eventId,
        String eventType,
        String payload
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.receivedAt = Instant.now();
        this.status = InboxStatus.PENDING;
    }

    // Getters and Setters
    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public InboxStatus getStatus() {
        return status;
    }

    public void setStatus(InboxStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void markProcessed() {
        this.status = InboxStatus.PROCESSED;
        this.processedAt = Instant.now();
    }

    public void markFailed(String errorMessage) {
        this.status = InboxStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}
