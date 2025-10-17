package com.knight.contexts.serviceprofiles.management.infra.persistence;

import com.knight.contexts.serviceprofiles.management.app.repository.ServicingProfileRepository;
import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of ServicingProfileRepository.
 * For MVP/testing. Replace with JPA implementation for production.
 */
@Repository
public class InMemoryServicingProfileRepository implements ServicingProfileRepository {

    private final Map<String, ServicingProfile> store = new ConcurrentHashMap<>();

    @Override
    public void save(ServicingProfile profile) {
        store.put(profile.profileId().urn(), profile);
    }

    @Override
    public Optional<ServicingProfile> findById(ServicingProfileId profileId) {
        return Optional.ofNullable(store.get(profileId.urn()));
    }

    @Override
    public Optional<ServicingProfile> findByClientId(ClientId clientId) {
        return store.values().stream()
            .filter(p -> p.clientId().equals(clientId))
            .findFirst();
    }

    @Override
    public void delete(ServicingProfileId profileId) {
        store.remove(profileId.urn());
    }
}
