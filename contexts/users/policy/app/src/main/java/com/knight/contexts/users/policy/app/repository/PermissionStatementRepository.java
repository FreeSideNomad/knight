package com.knight.contexts.users.policy.app.repository;

import com.knight.contexts.users.policy.domain.aggregate.PermissionStatement;
import com.knight.platform.sharedkernel.ProfileId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PermissionStatement aggregate.
 * To be implemented by infra layer.
 */
public interface PermissionStatementRepository {

    void save(PermissionStatement statement);

    Optional<PermissionStatement> findById(String statementId);

    List<PermissionStatement> findByProfileId(ProfileId profileId);

    void delete(String statementId);
}
