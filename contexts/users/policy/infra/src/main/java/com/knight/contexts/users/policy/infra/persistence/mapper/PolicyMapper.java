package com.knight.contexts.users.policy.infra.persistence.mapper;

import com.knight.contexts.users.policy.domain.aggregate.Policy;
import com.knight.contexts.users.policy.infra.persistence.entity.PolicyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jsr330")
public interface PolicyMapper {

    // Entity to Domain
    Policy toDomain(PolicyEntity entity);

    // Domain to Entity
    @Mapping(target = "version", ignore = true)
    PolicyEntity toEntity(Policy domain);

    // PolicyType enum mappings
    default PolicyEntity.PolicyType mapPolicyType(Policy.PolicyType policyType) {
        if (policyType == null) return null;
        return PolicyEntity.PolicyType.valueOf(policyType.name());
    }

    default Policy.PolicyType mapPolicyType(PolicyEntity.PolicyType policyType) {
        if (policyType == null) return null;
        return Policy.PolicyType.valueOf(policyType.name());
    }
}
