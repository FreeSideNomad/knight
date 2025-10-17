package com.knight.contexts.serviceprofiles.management.infra.persistence;

import com.knight.contexts.serviceprofiles.management.app.service.SpmApplicationService;
import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.platform.sharedkernel.ServicingProfileId;
import io.micronaut.context.annotation.Secondary;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for ServicingProfile (MVP - replace with JPA in production).
 * Marked as @Secondary so JPA repository takes precedence when available.
 */
@Singleton
@Secondary
public class InMemoryServicingProfileRepository implements SpmApplicationService.SpmRepository {

    private final Map<String, ServicingProfile> store = new ConcurrentHashMap<>();

    @Override
    public void save(ServicingProfile profile) {
        store.put(profile.getProfileId().urn(), profile);
    }

    @Override
    public Optional<ServicingProfile> findById(ServicingProfileId profileId) {
        return Optional.ofNullable(store.get(profileId.urn()));
    }
}
