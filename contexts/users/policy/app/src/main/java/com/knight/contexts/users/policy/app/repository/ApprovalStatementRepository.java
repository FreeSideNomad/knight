package com.knight.contexts.users.policy.app.repository;

import com.knight.contexts.users.policy.domain.aggregate.ApprovalStatement;
import com.knight.platform.sharedkernel.ProfileId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ApprovalStatement aggregate.
 * To be implemented by infra layer.
 */
public interface ApprovalStatementRepository {

    void save(ApprovalStatement statement);

    Optional<ApprovalStatement> findById(String statementId);

    List<ApprovalStatement> findByProfileId(ProfileId profileId);

    void delete(String statementId);
}
