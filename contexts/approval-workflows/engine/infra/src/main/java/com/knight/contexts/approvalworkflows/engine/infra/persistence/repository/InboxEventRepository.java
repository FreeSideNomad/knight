package com.knight.contexts.approvalworkflows.engine.infra.persistence.repository;

import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.InboxEventEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for Inbox events.
 */
@Repository
public interface InboxEventRepository extends JpaRepository<InboxEventEntity, UUID> {

    /**
     * Check if event has already been processed (for idempotency).
     */
    boolean existsByEventId(UUID eventId);

    /**
     * Count events by status (for monitoring).
     */
    long countByStatus(InboxEventEntity.InboxStatus status);
}
