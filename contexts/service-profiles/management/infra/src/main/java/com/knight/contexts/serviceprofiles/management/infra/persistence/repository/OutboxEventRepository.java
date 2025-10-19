package com.knight.contexts.serviceprofiles.management.infra.persistence.repository;

import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.OutboxEventEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

import java.util.UUID;

/**
 * Repository for Outbox events.
 */
@Repository
public interface OutboxEventRepository extends CrudRepository<OutboxEventEntity, UUID> {

    /**
     * Find pending events ordered by creation time.
     * Used by outbox publisher to process events in order.
     */
    @Query(value = "SELECT o FROM OutboxEventEntity o WHERE o.status = :status ORDER BY o.createdAt ASC",
           countQuery = "SELECT COUNT(o) FROM OutboxEventEntity o WHERE o.status = :status")
    Page<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(
        OutboxEventEntity.OutboxStatus status,
        Pageable pageable
    );

    /**
     * Count pending events (for monitoring).
     */
    long countByStatus(OutboxEventEntity.OutboxStatus status);
}
