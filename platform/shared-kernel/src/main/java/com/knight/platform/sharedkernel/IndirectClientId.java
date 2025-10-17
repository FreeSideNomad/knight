package com.knight.platform.sharedkernel;

/**
 * Indirect client identifier: IndirectClientId(clientId, sequence).
 * Sequence unique within direct client.
 * Stored as URN: ind-client:{client_urn}:{sequence}
 */
public final class IndirectClientId {
    private final ClientId clientId;
    private final int sequence;
    private final String urn;

    private IndirectClientId(ClientId clientId, int sequence) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId cannot be null");
        }
        if (!clientId.isSrf()) {
            throw new IllegalArgumentException(
                "IndirectClient requires SRF parent client. Got: " + clientId.system()
            );
        }
        if (sequence < 1) {
            throw new IllegalArgumentException("sequence must be positive integer. Got: " + sequence);
        }
        this.clientId = clientId;
        this.sequence = sequence;
        this.urn = "ind-client:" + clientId.urn() + ":" + sequence;
    }

    public static IndirectClientId of(ClientId clientId, int sequence) {
        return new IndirectClientId(clientId, sequence);
    }

    public static IndirectClientId fromUrn(String urn) {
        if (urn == null || !urn.startsWith("ind-client:")) {
            throw new IllegalArgumentException("Invalid IndirectClientId URN: " + urn);
        }
        String remainder = urn.substring("ind-client:".length());
        int lastColon = remainder.lastIndexOf(':');
        if (lastColon == -1) {
            throw new IllegalArgumentException("Invalid IndirectClientId URN format: " + urn);
        }
        String clientUrn = remainder.substring(0, lastColon);
        int sequence = Integer.parseInt(remainder.substring(lastColon + 1));
        return new IndirectClientId(ClientId.of(clientUrn), sequence);
    }

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
        if (!(o instanceof IndirectClientId that)) return false;
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
