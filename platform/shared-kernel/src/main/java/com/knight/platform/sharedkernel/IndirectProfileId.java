package com.knight.platform.sharedkernel;

/**
 * Indirect profile identifier: IndirectProfileId(clientId, indirectClientId).
 * Stored as URN: indirect:{indirect_client_urn}
 */
public final class IndirectProfileId implements ProfileId {
    private final ClientId clientId;
    private final IndirectClientId indirectClientId;
    private final String urn;

    private IndirectProfileId(ClientId clientId, IndirectClientId indirectClientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId cannot be null");
        }
        if (indirectClientId == null) {
            throw new IllegalArgumentException("indirectClientId cannot be null");
        }
        if (!clientId.isSrf()) {
            throw new IllegalArgumentException(
                "IndirectProfile requires SRF parent client. Got: " + clientId.system()
            );
        }
        if (!clientId.equals(indirectClientId.clientId())) {
            throw new IllegalArgumentException(
                "clientId must match indirectClientId.clientId(). Got: " + clientId + " vs " + indirectClientId.clientId()
            );
        }
        this.clientId = clientId;
        this.indirectClientId = indirectClientId;
        this.urn = "indirect:" + indirectClientId.urn();
    }

    public static IndirectProfileId of(ClientId clientId, IndirectClientId indirectClientId) {
        return new IndirectProfileId(clientId, indirectClientId);
    }

    public static IndirectProfileId fromUrn(String urn) {
        if (urn == null || !urn.startsWith("indirect:")) {
            throw new IllegalArgumentException("Invalid IndirectProfileId URN: " + urn);
        }
        String indirectClientUrn = urn.substring("indirect:".length());
        IndirectClientId indirectClientId = IndirectClientId.fromUrn(indirectClientUrn);
        return new IndirectProfileId(indirectClientId.clientId(), indirectClientId);
    }

    @Override
    public String urn() {
        return urn;
    }

    public ClientId clientId() {
        return clientId;
    }

    public IndirectClientId indirectClientId() {
        return indirectClientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndirectProfileId that)) return false;
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
