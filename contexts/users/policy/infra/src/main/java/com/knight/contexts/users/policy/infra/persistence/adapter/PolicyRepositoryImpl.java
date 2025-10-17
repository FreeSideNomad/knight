package com.knight.contexts.users.policy.infra.persistence.adapter;

import com.knight.contexts.users.policy.app.service.PolicyApplicationService;
import com.knight.contexts.users.policy.domain.aggregate.Policy;
import com.knight.contexts.users.policy.infra.persistence.mapper.PolicyMapper;
import com.knight.contexts.users.policy.infra.persistence.repository.PolicyJpaRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * JPA-based implementation of PolicyRepository.
 * Adapter between domain repository interface and JPA repository.
 */
@Singleton
public class PolicyRepositoryImpl implements PolicyApplicationService.PolicyRepository {

    private static final Logger LOG = LoggerFactory.getLogger(PolicyRepositoryImpl.class);

    private final PolicyJpaRepository jpaRepository;
    private final PolicyMapper mapper;

    public PolicyRepositoryImpl(PolicyJpaRepository jpaRepository, PolicyMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void save(Policy policy) {
        LOG.debug("Saving policy: {}", policy.getPolicyId());

        var entity = mapper.toEntity(policy);
        jpaRepository.save(entity);

        LOG.info("Policy saved: {}", policy.getPolicyId());
    }

    @Override
    public Optional<Policy> findById(String policyId) {
        LOG.debug("Finding policy by ID: {}", policyId);

        return jpaRepository.findById(policyId)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(String policyId) {
        LOG.debug("Deleting policy by ID: {}", policyId);

        jpaRepository.deleteById(policyId);

        LOG.info("Policy deleted: {}", policyId);
    }
}
