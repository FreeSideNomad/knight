package com.knight.platform.sharedkernel;

import java.util.UUID;

/**
 * User group identifier: UserGroupId(uuid).
 * Stored as URN: user-group:{uuid}
 */
public final class UserGroupId {
    private final String uuid;
    private final String urn;

    private UserGroupId(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("uuid cannot be null or blank");
        }
        this.uuid = uuid;
        this.urn = "user-group:" + uuid;
    }

    public static UserGroupId of(String uuid) {
        return new UserGroupId(uuid);
    }

    public static UserGroupId generate() {
        return new UserGroupId(UUID.randomUUID().toString());
    }

    public static UserGroupId fromUrn(String urn) {
        if (urn == null || !urn.startsWith("user-group:")) {
            throw new IllegalArgumentException("Invalid UserGroupId URN: " + urn);
        }
        String uuid = urn.substring("user-group:".length());
        return new UserGroupId(uuid);
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
        if (!(o instanceof UserGroupId that)) return false;
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
