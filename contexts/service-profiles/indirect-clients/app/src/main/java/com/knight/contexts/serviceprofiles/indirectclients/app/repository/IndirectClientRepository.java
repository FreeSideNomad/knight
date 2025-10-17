package com.knight.contexts.serviceprofiles.indirectclients.app.repository;

import com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate.IndirectClient;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for IndirectClient aggregate.
 * To be implemented by infra layer.
 */
public interface IndirectClientRepository {

    void save(IndirectClient indirectClient);

    Optional<IndirectClient> findById(IndirectClientId indirectClientId);

    List<IndirectClient> findByParentClientId(ClientId parentClientId);

    void delete(IndirectClientId indirectClientId);
}
