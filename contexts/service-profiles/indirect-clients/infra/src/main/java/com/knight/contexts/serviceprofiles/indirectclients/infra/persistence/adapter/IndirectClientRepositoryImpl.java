package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.adapter;

import com.knight.contexts.serviceprofiles.indirectclients.app.service.IndirectClientApplicationService;
import com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate.IndirectClient;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.mapper.IndirectClientMapper;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.repository.IndirectClientJpaRepository;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Adapter implementation of repository interface.
 * Converts between domain aggregates and JPA entities.
 */
@Singleton
public class IndirectClientRepositoryImpl implements IndirectClientApplicationService.IndirectClientRepository {

    private final IndirectClientJpaRepository jpaRepository;
    private final IndirectClientMapper mapper;

    public IndirectClientRepositoryImpl(
        IndirectClientJpaRepository jpaRepository,
        IndirectClientMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(IndirectClient client) {
        var entity = mapper.toEntity(client);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<IndirectClient> findById(IndirectClientId indirectClientId) {
        return jpaRepository.findById(indirectClientId.urn())
            .map(mapper::toDomain);
    }

    @Override
    public int getNextSequenceForClient(ClientId parentClientId) {
        // Count existing indirect clients for this parent and increment
        long count = jpaRepository.countByParentClientId(parentClientId.urn());
        return (int) count + 1;
    }
}
