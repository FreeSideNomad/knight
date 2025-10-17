package com.knight.contexts.serviceprofiles.management.api.events;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.time.Instant;

/**
 * Domain event published when a servicing profile is created.
 */
public record ServicingProfileCreated(
    ServicingProfileId profileId,
    ClientId clientId,
    String createdBy,
    Instant createdAt
) {}
