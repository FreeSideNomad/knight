package com.knight.contexts.serviceprofiles.indirectclients.api.events;

import java.time.Instant;

public record IndirectClientOnboarded(
    String indirectClientId,
    String parentClientId,
    String businessName,
    Instant onboardedAt
) {}
