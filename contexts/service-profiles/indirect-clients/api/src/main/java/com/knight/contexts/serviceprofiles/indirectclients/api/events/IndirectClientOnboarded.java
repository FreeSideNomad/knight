package com.knight.contexts.serviceprofiles.indirectclients.api.events;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;

import java.time.Instant;

public record IndirectClientOnboarded(
    IndirectClientId indirectClientId,
    ClientId parentClientId,
    String businessName,
    String createdBy,
    Instant createdAt
) {}
