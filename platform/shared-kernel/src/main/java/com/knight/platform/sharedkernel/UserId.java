package com.knight.platform.sharedkernel;

import java.util.UUID;

/**
 * User identifier: UserId(uuid).
 * Stored as URN: user:{uuid}
 */
public final class UserId {
    private final String uuid;
    private final String urn;

    private UserId(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("uuid cannot be null or blank");
        }
        this.uuid = uuid;
        this.urn = "user:" + uuid;
    }

    public static UserId of(String uuid) {
        return new UserId(uuid);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public static UserId fromUrn(String urn) {
        if (urn == null || !urn.startsWith("user:")) {
            throw new IllegalArgumentException("Invalid UserId URN: " + urn);
        }
        String uuid = urn.substring("user:".length());
        return new UserId(uuid);
    }

    public String urn() {
        return urn;
    }

    public String uuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId that)) return false;
        return urn.equals(that.urn);
    }

    @Override
    public int hashCode() {
        return urn.hashCode();
    }

    @Override
    public String toString() {
        return urn;
    }
}
