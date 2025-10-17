package com.knight.platform.sharedkernel;

import java.util.regex.Pattern;

/**
 * Client identifier as URN: {system}:{client_number}
 * where system is srf, gid, or ind.
 * Example: srf:12345, gid:G789, ind:IND001
 */
public final class ClientId {
    private static final Pattern URN_PATTERN = Pattern.compile("^(srf|gid|ind):[A-Za-z0-9_-]+$");

    private final String urn;
    private final String system;
    private final String clientNumber;

    private ClientId(String urn) {
        if (urn == null || urn.isBlank()) {
            throw new IllegalArgumentException("ClientId URN cannot be null or blank");
        }
        if (!URN_PATTERN.matcher(urn).matches()) {
            throw new IllegalArgumentException(
                "Invalid ClientId format. Expected {system}:{client_number} where system is srf, gid, or ind. Got: " + urn
            );
        }

        this.urn = urn;
        String[] parts = urn.split(":", 2);
        this.system = parts[0];
        this.clientNumber = parts[1];
    }

    public static ClientId of(String urn) {
        return new ClientId(urn);
    }

    public static ClientId srf(String clientNumber) {
        return new ClientId("srf:" + clientNumber);
    }

    public static ClientId gid(String clientNumber) {
        return new ClientId("gid:" + clientNumber);
    }

    public static ClientId ind(String clientNumber) {
        return new ClientId("ind:" + clientNumber);
    }

    public String urn() {
        return urn;
    }

    public String system() {
        return system;
    }

    public String clientNumber() {
        return clientNumber;
    }

    public boolean isSrf() {
        return "srf".equals(system);
    }

    public boolean isGid() {
        return "gid".equals(system);
    }

    public boolean isInd() {
        return "ind".equals(system);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientId clientId)) return false;
        return urn.equals(clientId.urn);
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
