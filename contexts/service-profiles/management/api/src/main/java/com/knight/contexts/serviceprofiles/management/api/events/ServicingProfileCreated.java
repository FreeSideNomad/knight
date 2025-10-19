package com.knight.contexts.serviceprofiles.management.api.events;

import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;

/**
 * Domain event published when a servicing profile is created.
 */
@Serdeable
public record ServicingProfileCreated(
    String profileId,
    String clientId,
    String status,
    String createdBy,
    Instant createdAt
) {}
