package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.mapper;

import com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate.IndirectClient;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.IndirectClientJpaEntity;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.RelatedPersonJpaEntity;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between domain and JPA entities.
 * Cannot use MapStruct because domain aggregate has private constructor.
 */
@Singleton
public class IndirectClientMapper {

    /**
     * Convert domain aggregate to JPA entity.
     */
    public IndirectClientJpaEntity toEntity(IndirectClient domain) {
        if (domain == null) return null;

        IndirectClientJpaEntity entity = new IndirectClientJpaEntity();
        entity.setIndirectClientId(domain.getIndirectClientId().urn());
        entity.setParentClientId(domain.getParentClientId().urn());
        entity.setClientType(IndirectClientJpaEntity.ClientType.valueOf(domain.getClientType().name()));
        entity.setBusinessName(domain.getBusinessName());
        entity.setTaxId(domain.getTaxId());
        entity.setStatus(IndirectClientJpaEntity.Status.valueOf(domain.getStatus().name()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // Map related persons
        List<RelatedPersonJpaEntity> relatedPersons = domain.getRelatedPersons().stream()
            .map(rp -> {
                RelatedPersonJpaEntity personEntity = new RelatedPersonJpaEntity();
                personEntity.setPersonId(rp.getPersonId());
                personEntity.setIndirectClient(entity);
                personEntity.setName(rp.getName());
                personEntity.setRole(rp.getRole());
                personEntity.setEmail(rp.getEmail());
                personEntity.setAddedAt(rp.getAddedAt());
                return personEntity;
            })
            .collect(Collectors.toList());
        entity.setRelatedPersons(relatedPersons);

        return entity;
    }

    /**
     * Convert JPA entity to domain aggregate.
     * NOTE: This reconstitution is simplified - in production you'd need
     * reflection or a reconstitution constructor to properly restore domain state.
     */
    public IndirectClient toDomain(IndirectClientJpaEntity entity) {
        if (entity == null) return null;

        IndirectClientId indirectClientId = IndirectClientId.fromUrn(entity.getIndirectClientId());
        ClientId parentClientId = ClientId.of(entity.getParentClientId());

        // Create new indirect client - this will be in PENDING status
        IndirectClient client = IndirectClient.create(
            indirectClientId,
            parentClientId,
            entity.getBusinessName(),
            entity.getTaxId()
        );

        // Re-add related persons to restore state
        // NOTE: This is a workaround since we can't directly set private fields
        // In production, consider adding a reconstitution method or using reflection
        if (entity.getRelatedPersons() != null) {
            for (RelatedPersonJpaEntity person : entity.getRelatedPersons()) {
                client.addRelatedPerson(person.getName(), person.getRole(), person.getEmail());
            }
        }

        // Restore status if needed (domain methods may have changed it)
        // This would ideally be handled by a reconstitution constructor

        return client;
    }
}
