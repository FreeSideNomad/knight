package com.knight.platform.sharedkernel;

/**
 * Online profile identifier: OnlineProfileId(clientId, sequence).
 * Sequence unique within client. Stored as URN: online:{client_urn}:{sequence}
 */
public final class OnlineProfileId implements ProfileId {
    private final ClientId clientId;
    private final int sequence;
    private final String urn;

    private OnlineProfileId(ClientId clientId, int sequence) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId cannot be null");
        }
        if (!clientId.isSrf()) {
            throw new IllegalArgumentException(
                "OnlineProfile requires SRF client. Got: " + clientId.system()
            );
        }
        if (sequence < 1) {
            throw new IllegalArgumentException("sequence must be positive integer. Got: " + sequence);
        }
        this.clientId = clientId;
        this.sequence = sequence;
        this.urn = "online:" + clientId.urn() + ":" + sequence;
    }

    public static OnlineProfileId of(ClientId clientId, int sequence) {
        return new OnlineProfileId(clientId, sequence);
    }

    public static OnlineProfileId fromUrn(String urn) {
        if (urn == null || !urn.startsWith("online:")) {
            throw new IllegalArgumentException("Invalid OnlineProfileId URN: " + urn);
        }
        String remainder = urn.substring("online:".length());
        int lastColon = remainder.lastIndexOf(':');
        if (lastColon == -1) {
            throw new IllegalArgumentException("Invalid OnlineProfileId URN format: " + urn);
        }
        String clientUrn = remainder.substring(0, lastColon);
        int sequence = Integer.parseInt(remainder.substring(lastColon + 1));
        return new OnlineProfileId(ClientId.of(clientUrn), sequence);
    }

    @Override
    public String urn() {
        return urn;
    }

    public ClientId clientId() {
        return clientId;
    }

    public int sequence() {
        return sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OnlineProfileId that)) return false;
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
