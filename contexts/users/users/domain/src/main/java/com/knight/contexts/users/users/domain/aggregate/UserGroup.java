package com.knight.contexts.users.users.domain.aggregate;

import com.knight.platform.sharedkernel.UserGroupId;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UserGroup aggregate root.
 * Manages user group membership and properties.
 *
 * Invariants:
 * - group_id must be unique
 * - all members must belong to the same profile
 * - profile_id cannot change after creation
 */
public class UserGroup {

    private final UserGroupId groupId;
    private final ServicingProfileId profileId;
    private String name;
    private String description;
    private final List<UserGroupMembership> members;
    private final Instant createdAt;
    private Instant updatedAt;

    private UserGroup(UserGroupId groupId, ServicingProfileId profileId, String name, String description) {
        if (groupId == null) {
            throw new IllegalArgumentException("groupId cannot be null");
        }
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }

        this.groupId = groupId;
        this.profileId = profileId;
        this.name = name;
        this.description = description;
        this.members = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Factory method to create a new user group.
     */
    public static UserGroup create(UserGroupId groupId, ServicingProfileId profileId,
                                  String name, String description) {
        return new UserGroup(groupId, profileId, name, description);
    }

    /**
     * Add member to group.
     * Validates that user belongs to same profile as group.
     */
    public UserGroupMembership addMember(UserId userId, ServicingProfileId userProfileId) {
        if (!this.profileId.equals(userProfileId)) {
            throw new IllegalArgumentException(
                "User profile does not match group profile. Group: " + this.profileId + ", User: " + userProfileId
            );
        }

        // Check if already a member
        boolean alreadyMember = members.stream()
            .anyMatch(m -> m.userId().equals(userId));

        if (alreadyMember) {
            throw new IllegalArgumentException("User is already a member of this group: " + userId);
        }

        String membershipId = UUID.randomUUID().toString();
        UserGroupMembership membership = new UserGroupMembership(
            membershipId,
            groupId,
            userId,
            Instant.now()
        );

        members.add(membership);
        this.updatedAt = Instant.now();

        return membership;
    }

    /**
     * Remove member from group.
     */
    public void removeMember(UserId userId) {
        UserGroupMembership membership = members.stream()
            .filter(m -> m.userId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("User is not a member of this group: " + userId));

        members.remove(membership);
        this.updatedAt = Instant.now();
    }

    /**
     * Update group name.
     */
    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }
        this.name = newName;
        this.updatedAt = Instant.now();
    }

    /**
     * Update group description.
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UserGroupId groupId() { return groupId; }
    public ServicingProfileId profileId() { return profileId; }
    public String name() { return name; }
    public String description() { return description; }
    public List<UserGroupMembership> members() { return List.copyOf(members); }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    /**
     * UserGroupMembership entity within UserGroup aggregate.
     */
    public static class UserGroupMembership {
        private final String membershipId;
        private final UserGroupId groupId;
        private final UserId userId;
        private final Instant joinedAt;

        UserGroupMembership(String membershipId, UserGroupId groupId, UserId userId, Instant joinedAt) {
            this.membershipId = membershipId;
            this.groupId = groupId;
            this.userId = userId;
            this.joinedAt = joinedAt;
        }

        // Getters
        public String membershipId() { return membershipId; }
        public UserGroupId groupId() { return groupId; }
        public UserId userId() { return userId; }
        public Instant joinedAt() { return joinedAt; }
    }
}
