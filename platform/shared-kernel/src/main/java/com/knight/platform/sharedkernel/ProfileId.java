package com.knight.platform.sharedkernel;

/**
 * Base interface for profile identifiers.
 * All profiles stored as URN for polymorphic handling.
 * Implementations: ServicingProfileId, OnlineProfileId, IndirectProfileId
 */
public sealed interface ProfileId permits ServicingProfileId, OnlineProfileId, IndirectProfileId {
    String urn();
}
