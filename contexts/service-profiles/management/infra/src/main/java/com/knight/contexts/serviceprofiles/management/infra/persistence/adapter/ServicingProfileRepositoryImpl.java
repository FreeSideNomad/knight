package com.knight.contexts.serviceprofiles.management.infra.persistence.adapter;

import com.knight.contexts.serviceprofiles.management.app.service.SpmApplicationService;
import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.contexts.serviceprofiles.management.infra.persistence.mapper.ServicingProfileMapper;
import com.knight.contexts.serviceprofiles.management.infra.persistence.repository.ServicingProfileJpaRepository;
import com.knight.platform.sharedkernel.ServicingProfileId;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Adapter implementation of repository interface.
 * Converts between domain aggregates and JPA entities.
 */
@Singleton
public class ServicingProfileRepositoryImpl implements SpmApplicationService.SpmRepository {

    private final ServicingProfileJpaRepository jpaRepository;
    private final ServicingProfileMapper mapper;

    public ServicingProfileRepositoryImpl(
        ServicingProfileJpaRepository jpaRepository,
        ServicingProfileMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(ServicingProfile profile) {
        var entity = mapper.toEntity(profile);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<ServicingProfile> findById(ServicingProfileId profileId) {
        return jpaRepository.findById(profileId.urn())
            .map(mapper::toDomain);
    }
}
