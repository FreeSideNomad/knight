package com.knight.contexts.serviceprofiles.management.infra.persistence.mapper;

import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.EnrolledAccountJpaEntity;
import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.EnrolledServiceJpaEntity;
import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.ServicingProfileJpaEntity;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between domain and JPA entities.
 * Cannot use MapStruct because domain aggregate has private constructor.
 */
@Singleton
public class ServicingProfileMapper {

    /**
     * Convert domain aggregate to JPA entity.
     */
    public ServicingProfileJpaEntity toEntity(ServicingProfile domain) {
        if (domain == null) return null;

        ServicingProfileJpaEntity entity = new ServicingProfileJpaEntity();
        entity.setProfileUrn(domain.getProfileId().urn());
        entity.setClientUrn(domain.getClientId().urn());
        entity.setStatus(ServicingProfileJpaEntity.Status.valueOf(domain.getStatus().name()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setCreatedBy(domain.getCreatedBy());

        // Map service enrollments
        List<EnrolledServiceJpaEntity> services = domain.getServiceEnrollments().stream()
            .map(se -> {
                EnrolledServiceJpaEntity serviceEntity = new EnrolledServiceJpaEntity();
                serviceEntity.setProfile(entity);
                serviceEntity.setEnrollmentId(se.getEnrollmentId());
                serviceEntity.setServiceType(se.getServiceType());
                serviceEntity.setConfiguration(se.getConfiguration());
                serviceEntity.setStatus(EnrolledServiceJpaEntity.Status.valueOf(se.getStatus().name()));
                serviceEntity.setEnrolledAt(se.getEnrolledAt());
                return serviceEntity;
            })
            .collect(Collectors.toList());
        entity.setEnrolledServices(services);

        // Map account enrollments
        List<EnrolledAccountJpaEntity> accounts = domain.getAccountEnrollments().stream()
            .map(ae -> {
                EnrolledAccountJpaEntity accountEntity = new EnrolledAccountJpaEntity();
                accountEntity.setProfile(entity);
                accountEntity.setEnrollmentId(ae.getEnrollmentId());
                accountEntity.setServiceEnrollmentId(ae.getServiceEnrollmentId());
                accountEntity.setAccountId(ae.getAccountId());
                accountEntity.setStatus(EnrolledAccountJpaEntity.Status.valueOf(ae.getStatus().name()));
                accountEntity.setEnrolledAt(ae.getEnrolledAt());
                return accountEntity;
            })
            .collect(Collectors.toList());
        entity.setEnrolledAccounts(accounts);

        return entity;
    }

    /**
     * Convert JPA entity to domain aggregate.
     * NOTE: This reconstitution is simplified - in production you'd need
     * reflection or a reconstitution constructor to properly restore domain state.
     */
    public ServicingProfile toDomain(ServicingProfileJpaEntity entity) {
        if (entity == null) return null;

        ServicingProfileId profileId = ServicingProfileId.fromUrn(entity.getProfileUrn());
        ClientId clientId = ClientId.of(entity.getClientUrn());

        // Create new profile - this will be in PENDING status
        ServicingProfile profile = ServicingProfile.create(profileId, clientId, entity.getCreatedBy());

        // Re-enroll services and accounts to restore state
        // NOTE: This is a workaround since we can't directly set private fields
        // In production, consider adding a reconstitution method or using reflection
        if (entity.getEnrolledServices() != null) {
            for (EnrolledServiceJpaEntity service : entity.getEnrolledServices()) {
                profile.enrollService(service.getServiceType(), service.getConfiguration());
            }
        }

        if (entity.getEnrolledAccounts() != null) {
            for (EnrolledAccountJpaEntity account : entity.getEnrolledAccounts()) {
                profile.enrollAccount(account.getServiceEnrollmentId(), account.getAccountId());
            }
        }

        return profile;
    }
}
