package com.knight.platform.sharedkernel;

/**
 * Servicing profile identifier: ServicingProfileId(clientId).
 * Stored as URN: servicing:{client_urn}
 */
public final class ServicingProfileId implements ProfileId {
    private final ClientId clientId;
    private final String urn;

    private ServicingProfileId(ClientId clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId cannot be null");
        }
        if (!clientId.isSrf() && !clientId.isGid()) {
            throw new IllegalArgumentException(
                "ServicingProfile requires SRF or GID client. Got: " + clientId.system()
            );
        }
        this.clientId = clientId;
        this.urn = "servicing:" + clientId.urn();
    }

    public static ServicingProfileId of(ClientId clientId) {
        return new ServicingProfileId(clientId);
    }

    public static ServicingProfileId fromUrn(String urn) {
        if (urn == null || !urn.startsWith("servicing:")) {
            throw new IllegalArgumentException("Invalid ServicingProfileId URN: " + urn);
        }
        String clientUrn = urn.substring("servicing:".length());
        return new ServicingProfileId(ClientId.of(clientUrn));
    }

    @Override
    public String urn() {
        return urn;
    }

    public ClientId clientId() {
        return clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServicingProfileId that)) return false;
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
