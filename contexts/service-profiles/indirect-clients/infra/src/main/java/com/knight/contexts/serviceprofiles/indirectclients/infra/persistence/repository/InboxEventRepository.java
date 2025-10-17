package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.repository;

import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.InboxEventEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for Inbox events.
 */
@Repository
public interface InboxEventRepository extends JpaRepository<InboxEventEntity, UUID> {

    /**
     * Check if event has already been received (idempotency).
     */
    boolean existsByEventId(UUID eventId);

    /**
     * Count failed events (for monitoring).
     */
    long countByStatus(InboxEventEntity.InboxStatus status);
}
