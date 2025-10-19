package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.mapper;

import com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate.IndirectClient;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.IndirectClientEntity;
import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.RelatedPersonEntity;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class IndirectClientMapper {

    // Entity to Domain
    public IndirectClient toDomain(IndirectClientEntity entity) {
        IndirectClientId indirectClientId = IndirectClientId.fromUrn(entity.getIndirectClientId());
        
        IndirectClient client = IndirectClient.create(
            indirectClientId,
            ClientId.of(entity.getParentClientId()),
            entity.getBusinessName(),
            entity.getTaxId()
        );

        // Add related persons
        if (entity.getRelatedPersons() != null) {
            for (RelatedPersonEntity personEntity : entity.getRelatedPersons()) {
                client.addRelatedPerson(
                    personEntity.getName(),
                    personEntity.getRole(),
                    personEntity.getEmail()
                );
            }
        }

        return client;
    }

    // Domain to Entity
    public IndirectClientEntity toEntity(IndirectClient domain) {
        IndirectClientEntity entity = new IndirectClientEntity(
            domain.getIndirectClientId().urn(),
            domain.getParentClientId().urn(),
            mapClientType(domain.getClientType()),
            domain.getBusinessName(),
            domain.getTaxId()
        );

        entity.setStatus(mapStatus(domain.getStatus()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // Map related persons
        if (domain.getRelatedPersons() != null) {
            List<RelatedPersonEntity> personEntities = domain.getRelatedPersons().stream()
                .map(person -> {
                    RelatedPersonEntity e = new RelatedPersonEntity();
                    e.setPersonId(person.getPersonId());
                    e.setName(person.getName());
                    e.setRole(person.getRole());
                    e.setEmail(person.getEmail());
                    e.setAddedAt(person.getAddedAt());
                    e.setIndirectClient(entity);
                    return e;
                })
                .collect(Collectors.toList());
            entity.setRelatedPersons(personEntities);
        }

        return entity;
    }

    // Status enum mappings
    private IndirectClientEntity.Status mapStatus(IndirectClient.Status status) {
        if (status == null) return null;
        return IndirectClientEntity.Status.valueOf(status.name());
    }

    // ClientType enum mappings
    private IndirectClientEntity.ClientType mapClientType(IndirectClient.ClientType clientType) {
        if (clientType == null) return null;
        return IndirectClientEntity.ClientType.valueOf(clientType.name());
    }
}
