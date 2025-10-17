package com.knight.contexts.serviceprofiles.management.app.repository;

import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.util.Optional;

/**
 * Repository interface for ServicingProfile aggregate.
 * To be implemented by infra layer.
 */
public interface ServicingProfileRepository {

    void save(ServicingProfile profile);

    Optional<ServicingProfile> findById(ServicingProfileId profileId);

    Optional<ServicingProfile> findByClientId(ClientId clientId);

    void delete(ServicingProfileId profileId);
}
