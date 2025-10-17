package com.knight.platform.sharedkernel;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Client identifier as URN: {system}:{client_number}
 * where system is srf, gid, or ind.
 * Example: srf:12345, gid:G789, ind:IND001
 */
public final class ClientId {
    private static final Pattern PATTERN = Pattern.compile("^(srf|gid|ind):[A-Za-z0-9_-]+$");

    private final String urn;

    private ClientId(String urn) {
        if (urn == null || !PATTERN.matcher(urn).matches()) {
            throw new IllegalArgumentException(
                "Invalid ClientId format. Expected: {system}:{client_number} where system is srf, gid, or ind");
        }
        this.urn = urn;
    }

    public static ClientId of(String urn) {
        return new ClientId(urn);
    }

    public String urn() {
        return urn;
    }

    public String system() {
        return urn.split(":")[0];
    }

    public String clientNumber() {
        return urn.split(":")[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientId clientId = (ClientId) o;
        return urn.equals(clientId.urn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(urn);
    }

    @Override
    public String toString() {
        return "ClientId{" + urn + "}";
    }
}
