package com.knight.contexts.users.policy.api.events;

import java.time.Instant;

public record PolicyCreated(
    String policyId,
    String policyType,
    String subject,
    Instant createdAt
) {}
