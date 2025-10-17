package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence;

import com.knight.contexts.serviceprofiles.indirectclients.app.repository.IndirectClientRepository;
import com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate.IndirectClient;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of IndirectClientRepository.
 * For MVP/testing. Replace with JPA implementation for production.
 */
@Repository
public class InMemoryIndirectClientRepository implements IndirectClientRepository {

    private final Map<String, IndirectClient> store = new ConcurrentHashMap<>();

    @Override
    public void save(IndirectClient indirectClient) {
        store.put(indirectClient.indirectClientId().urn(), indirectClient);
    }

    @Override
    public Optional<IndirectClient> findById(IndirectClientId indirectClientId) {
        return Optional.ofNullable(store.get(indirectClientId.urn()));
    }

    @Override
    public List<IndirectClient> findByParentClientId(ClientId parentClientId) {
        return store.values().stream()
            .filter(ic -> ic.parentClientId().equals(parentClientId))
            .toList();
    }

    @Override
    public void delete(IndirectClientId indirectClientId) {
        store.remove(indirectClientId.urn());
    }
}
