package com.knight.contexts.serviceprofiles.management.infra.persistence.mapper;

import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.AccountEnrollmentEntity;
import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.ServiceEnrollmentEntity;
import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.ServicingProfileEntity;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import jakarta.inject.Singleton;

import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class ServicingProfileMapper {

    // Entity to Domain - manual mapping since domain has private constructors
    public ServicingProfile toDomain(ServicingProfileEntity entity) {
        // Create using factory method - need to reconstruct from stored URN
        ServicingProfileId profileId = ServicingProfileId.fromUrn(entity.getProfileId());

        ServicingProfile profile = ServicingProfile.create(
            profileId,
            profileId.clientId(),
            entity.getCreatedBy()
        );

        // Restore status and timestamps
        profile.setStatus(mapStatus(entity.getStatus()));
        profile.setUpdatedAt(entity.getUpdatedAt());

        // Reconstitute service enrollments with original IDs
        if (entity.getServiceEnrollments() != null) {
            for (ServiceEnrollmentEntity seEntity : entity.getServiceEnrollments()) {
                ServicingProfile.ServiceEnrollment enrollment = ServicingProfile.ServiceEnrollment.reconstitute(
                    seEntity.getEnrollmentId(),
                    seEntity.getServiceType(),
                    seEntity.getConfiguration(),
                    mapStatus(seEntity.getStatus()),
                    seEntity.getEnrolledAt()
                );
                profile.addExistingServiceEnrollment(enrollment);
            }
        }

        // Reconstitute account enrollments with original IDs
        if (entity.getAccountEnrollments() != null) {
            for (AccountEnrollmentEntity aeEntity : entity.getAccountEnrollments()) {
                ServicingProfile.AccountEnrollment enrollment = ServicingProfile.AccountEnrollment.reconstitute(
                    aeEntity.getEnrollmentId(),
                    aeEntity.getServiceEnrollmentId(),
                    aeEntity.getAccountId(),
                    mapStatus(aeEntity.getStatus()),
                    aeEntity.getEnrolledAt()
                );
                profile.addExistingAccountEnrollment(enrollment);
            }
        }

        return profile;
    }

    // Domain to Entity
    public ServicingProfileEntity toEntity(ServicingProfile domain) {
        ServicingProfileEntity entity = new ServicingProfileEntity(
            domain.getProfileId().urn(),
            domain.getClientId().urn(),
            domain.getCreatedBy()
        );

        entity.setStatus(mapStatus(domain.getStatus()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // Map service enrollments
        if (domain.getServiceEnrollments() != null) {
            Set<ServiceEnrollmentEntity> seEntities = domain.getServiceEnrollments().stream()
                .map(se -> {
                    ServiceEnrollmentEntity e = new ServiceEnrollmentEntity();
                    e.setEnrollmentId(se.getEnrollmentId());
                    e.setServiceType(se.getServiceType());
                    e.setConfiguration(se.getConfiguration());
                    e.setStatus(mapStatus(se.getStatus()));
                    e.setEnrolledAt(se.getEnrolledAt());
                    e.setServicingProfile(entity);
                    return e;
                })
                .collect(Collectors.toSet());
            entity.setServiceEnrollments(seEntities);
        }

        // Map account enrollments
        if (domain.getAccountEnrollments() != null) {
            Set<AccountEnrollmentEntity> aeEntities = domain.getAccountEnrollments().stream()
                .map(ae -> {
                    AccountEnrollmentEntity e = new AccountEnrollmentEntity();
                    e.setEnrollmentId(ae.getEnrollmentId());
                    e.setServiceEnrollmentId(ae.getServiceEnrollmentId());
                    e.setAccountId(ae.getAccountId());
                    e.setStatus(mapStatus(ae.getStatus()));
                    e.setEnrolledAt(ae.getEnrolledAt());
                    e.setServicingProfile(entity);
                    return e;
                })
                .collect(Collectors.toSet());
            entity.setAccountEnrollments(aeEntities);
        }

        return entity;
    }

    // Status enum mappings
    private ServicingProfileEntity.Status mapStatus(ServicingProfile.Status status) {
        if (status == null) return null;
        return ServicingProfileEntity.Status.valueOf(status.name());
    }

    private ServicingProfile.Status mapStatus(ServicingProfileEntity.Status status) {
        if (status == null) return null;
        return ServicingProfile.Status.valueOf(status.name());
    }
}
