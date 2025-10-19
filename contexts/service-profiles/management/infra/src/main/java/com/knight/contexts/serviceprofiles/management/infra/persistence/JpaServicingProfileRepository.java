package com.knight.contexts.serviceprofiles.management.infra.persistence;

import com.knight.contexts.serviceprofiles.management.app.service.SpmApplicationService;
import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.ServicingProfileEntity;
import com.knight.contexts.serviceprofiles.management.infra.persistence.mapper.ServicingProfileMapper;
import com.knight.contexts.serviceprofiles.management.infra.persistence.repository.ServicingProfileRepository;
import com.knight.platform.sharedkernel.ServicingProfileId;
import io.micronaut.context.annotation.Primary;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * JPA-based repository implementation for ServicingProfile.
 * Marked as @Primary to take precedence over InMemoryServicingProfileRepository.
 */
@Singleton
@Primary
public class JpaServicingProfileRepository implements SpmApplicationService.SpmRepository {

    private final ServicingProfileRepository jpaRepository;
    private final ServicingProfileMapper mapper;

    public JpaServicingProfileRepository(
        ServicingProfileRepository jpaRepository,
        ServicingProfileMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(ServicingProfile profile) {
        // Check if entity already exists
        Optional<ServicingProfileEntity> existing = jpaRepository.findByProfileId(profile.getProfileId().urn());

        if (existing.isPresent()) {
            // Update existing entity
            ServicingProfileEntity entity = existing.get();

            // Update scalar fields
            entity.setStatus(mapStatus(profile.getStatus()));
            entity.setUpdatedAt(profile.getUpdatedAt());

            // Clear and rebuild collections to handle orphan removal
            entity.getServiceEnrollments().clear();
            entity.getAccountEnrollments().clear();

            // Re-map enrollments from domain
            profile.getServiceEnrollments().forEach(se -> {
                var seEntity = new com.knight.contexts.serviceprofiles.management.infra.persistence.entity.ServiceEnrollmentEntity();
                seEntity.setEnrollmentId(se.getEnrollmentId());
                seEntity.setServiceType(se.getServiceType());
                seEntity.setConfiguration(se.getConfiguration());
                seEntity.setStatus(mapStatus(se.getStatus()));
                seEntity.setEnrolledAt(se.getEnrolledAt());
                seEntity.setServicingProfile(entity);
                entity.getServiceEnrollments().add(seEntity);
            });

            profile.getAccountEnrollments().forEach(ae -> {
                var aeEntity = new com.knight.contexts.serviceprofiles.management.infra.persistence.entity.AccountEnrollmentEntity();
                aeEntity.setEnrollmentId(ae.getEnrollmentId());
                aeEntity.setServiceEnrollmentId(ae.getServiceEnrollmentId());
                aeEntity.setAccountId(ae.getAccountId());
                aeEntity.setStatus(mapStatus(ae.getStatus()));
                aeEntity.setEnrolledAt(ae.getEnrolledAt());
                aeEntity.setServicingProfile(entity);
                entity.getAccountEnrollments().add(aeEntity);
            });

            jpaRepository.update(entity);
        } else {
            // Create new entity
            ServicingProfileEntity entity = mapper.toEntity(profile);
            jpaRepository.save(entity);
        }
    }

    @Override
    public Optional<ServicingProfile> findById(ServicingProfileId profileId) {
        return jpaRepository.findByProfileId(profileId.urn())
            .map(mapper::toDomain);
    }

    // Helper methods for status mapping
    private ServicingProfileEntity.Status mapStatus(ServicingProfile.Status status) {
        if (status == null) return null;
        return ServicingProfileEntity.Status.valueOf(status.name());
    }
}
