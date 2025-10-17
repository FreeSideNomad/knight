package com.knight.contexts.users.users.infra.persistence.repository;

import com.knight.contexts.users.users.infra.persistence.entity.InboxEventEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for Inbox events (idempotency).
 */
@Repository
public interface InboxEventRepository extends JpaRepository<InboxEventEntity, UUID> {

    /**
     * Check if event has already been processed.
     * Used for idempotent event consumption.
     */
    boolean existsByEventId(UUID eventId);
}
