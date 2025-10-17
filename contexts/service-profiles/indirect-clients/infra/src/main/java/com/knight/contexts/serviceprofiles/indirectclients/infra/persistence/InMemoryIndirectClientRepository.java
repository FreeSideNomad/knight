package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence;

import com.knight.contexts.serviceprofiles.indirectclients.app.service.IndirectClientApplicationService;
import com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate.IndirectClient;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import io.micronaut.context.annotation.Secondary;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory repository for IndirectClient (MVP - replace with JPA in production).
 * Marked as @Secondary so JPA implementation takes precedence when available.
 */
@Singleton
@Secondary
public class InMemoryIndirectClientRepository implements IndirectClientApplicationService.IndirectClientRepository {

    private final Map<String, IndirectClient> store = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> sequenceCounters = new ConcurrentHashMap<>();

    @Override
    public void save(IndirectClient client) {
        store.put(client.getIndirectClientId().urn(), client);
    }

    @Override
    public Optional<IndirectClient> findById(IndirectClientId id) {
        return Optional.ofNullable(store.get(id.urn()));
    }

    @Override
    public int getNextSequenceForClient(ClientId parentClientId) {
        return sequenceCounters
            .computeIfAbsent(parentClientId.urn(), k -> new AtomicInteger(0))
            .incrementAndGet();
    }
}
