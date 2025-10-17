package com.knight.contexts.users.policy.infra.persistence.mapper;

import com.knight.contexts.users.policy.domain.aggregate.Policy;
import com.knight.contexts.users.policy.infra.persistence.entity.PolicyJpaEntity;
import jakarta.inject.Singleton;

/**
 * Manual mapper between Policy domain aggregate and PolicyJpaEntity.
 */
@Singleton
public class PolicyMapper {

    /**
     * Map domain Policy to JPA entity.
     */
    public PolicyJpaEntity toEntity(Policy policy) {
        return new PolicyJpaEntity(
            policy.getPolicyId(),
            mapPolicyType(policy.getPolicyType()),
            policy.getSubject(),
            policy.getAction(),
            policy.getResource(),
            policy.getApproverCount(),
            policy.getCreatedAt(),
            policy.getUpdatedAt()
        );
    }

    /**
     * Map JPA entity to domain Policy.
     * Uses Policy.create() factory method to reconstruct the aggregate.
     */
    public Policy toDomain(PolicyJpaEntity entity) {
        // Note: Since Policy has a private constructor and uses factory method,
        // we need to use the create method. However, this creates a new policy with
        // new timestamps. For proper reconstruction, the domain model would need
        // a reconstitution method or constructor.

        // For now, using the create method as the domain doesn't provide
        // a reconstitution mechanism. This is a known limitation that should
        // be addressed in the domain layer if full persistence is needed.

        return Policy.create(
            mapPolicyType(entity.getPolicyType()),
            entity.getSubject(),
            entity.getAction(),
            entity.getResource(),
            entity.getApproverCount()
        );
    }

    // Enum mapping helpers
    private PolicyJpaEntity.PolicyType mapPolicyType(Policy.PolicyType domainType) {
        return PolicyJpaEntity.PolicyType.valueOf(domainType.name());
    }

    private Policy.PolicyType mapPolicyType(PolicyJpaEntity.PolicyType entityType) {
        return Policy.PolicyType.valueOf(entityType.name());
    }
}
