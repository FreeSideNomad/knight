package com.knight.contexts.users.users.api.events;

import java.time.Instant;

public record UserCreated(
    String userId,
    String email,
    String userType,
    String identityProvider,
    Instant createdAt
) {}
