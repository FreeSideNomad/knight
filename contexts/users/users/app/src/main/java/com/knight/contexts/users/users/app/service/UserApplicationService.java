package com.knight.contexts.users.users.app.service;

import com.knight.contexts.users.users.api.commands.UserCommands;
import com.knight.contexts.users.users.api.events.*;
import com.knight.contexts.users.users.api.queries.UserQueries;
import com.knight.contexts.users.users.app.repository.UserRepository;
import com.knight.contexts.users.users.app.repository.UserGroupRepository;
import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.contexts.users.users.domain.aggregate.UserGroup;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service implementing commands and queries for User Management.
 * Orchestrates domain operations, repository access, and event publishing.
 * Enforces dual admin rule for profile.
 */
@Service
public class UserApplicationService implements UserCommands, UserQueries {

    private final UserRepository userRepository;
    private final UserGroupRepository groupRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserApplicationService(UserRepository userRepository,
                                 UserGroupRepository groupRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public UserId createUser(CreateUserCmd cmd) {
        // Check if email already exists for this profile
        userRepository.findByEmailAndProfileId(cmd.email(), cmd.profileId()).ifPresent(existing -> {
            throw new IllegalArgumentException("User with email already exists for profile: " + cmd.email());
        });

        // Generate new user ID
        UserId userId = UserId.generate();

        // Parse role and source
        User.Role role = User.Role.valueOf(cmd.role());
        User.Source source = User.Source.valueOf(cmd.source());

        // Create aggregate
        User user = User.create(userId, cmd.profileId(), cmd.email(),
            cmd.firstName(), cmd.lastName(), role, source);

        // Activate immediately if source is OKTA (platform-owned)
        if (source == User.Source.OKTA) {
            user.activate();
        }

        // Save
        userRepository.save(user);

        // Publish event
        UserCreated event = new UserCreated(
            userId,
            cmd.profileId(),
            cmd.email(),
            cmd.firstName(),
            cmd.lastName(),
            cmd.role(),
            cmd.source(),
            user.createdAt()
        );
        eventPublisher.publishEvent(event);

        return userId;
    }

    @Override
    @Transactional
    public void lockUser(LockUserCmd cmd) {
        User user = userRepository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId()));

        user.lock(cmd.reason(), cmd.lockedBy());

        userRepository.save(user);

        // Publish event
        UserLocked event = new UserLocked(
            cmd.userId(),
            cmd.lockedBy(),
            cmd.reason(),
            user.updatedAt()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public void unlockUser(UnlockUserCmd cmd) {
        User user = userRepository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId()));

        user.unlock();

        userRepository.save(user);

        // Publish event
        UserUnlocked event = new UserUnlocked(
            cmd.userId(),
            user.updatedAt()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public void updateUserRole(UpdateUserRoleCmd cmd) {
        User user = userRepository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId()));

        User.Role newRole = User.Role.valueOf(cmd.newRole());

        // Enforce dual admin rule: prevent downgrading last admin
        if (user.role() == User.Role.ADMINISTRATOR && newRole == User.Role.REGULAR_USER) {
            List<User> admins = userRepository.findAdministratorsByProfileId(user.profileId());
            if (admins.size() <= 2) {
                throw new IllegalStateException(
                    "Cannot downgrade administrator. Profile must have at least 2 administrators."
                );
            }
        }

        user.updateRole(newRole);

        userRepository.save(user);

        // Publish event
        UserUpdated event = new UserUpdated(
            cmd.userId(),
            "role",
            cmd.newRole(),
            user.updatedAt()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public void deactivateUser(DeactivateUserCmd cmd) {
        User user = userRepository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId()));

        // Enforce dual admin rule: prevent deactivating last admin
        if (user.role() == User.Role.ADMINISTRATOR) {
            List<User> admins = userRepository.findAdministratorsByProfileId(user.profileId());
            if (admins.size() <= 2) {
                throw new IllegalStateException(
                    "Cannot deactivate administrator. Profile must have at least 2 administrators."
                );
            }
        }

        user.deactivate();

        userRepository.save(user);

        // Publish event
        UserDeactivated event = new UserDeactivated(
            cmd.userId(),
            user.updatedAt()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetail getUser(UserId userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return new UserDetail(
            user.userId().urn(),
            user.profileId().urn(),
            user.email(),
            user.firstName(),
            user.lastName(),
            user.role().name(),
            user.source().name(),
            user.status().name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummary> listUsersByProfile(ServicingProfileId profileId) {
        List<User> users = userRepository.findByProfileId(profileId);

        return users.stream()
            .map(u -> new UserSummary(
                u.userId().urn(),
                u.email(),
                u.firstName(),
                u.lastName(),
                u.role().name(),
                u.status().name()
            ))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummary> getAdministratorsForProfile(ServicingProfileId profileId) {
        List<User> admins = userRepository.findAdministratorsByProfileId(profileId);

        return admins.stream()
            .map(u -> new UserSummary(
                u.userId().urn(),
                u.email(),
                u.firstName(),
                u.lastName(),
                u.role().name(),
                u.status().name()
            ))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupSummary> getUserGroups(UserId userId) {
        List<UserGroup> groups = groupRepository.findByUserId(userId);

        return groups.stream()
            .map(g -> new GroupSummary(
                g.groupId().urn(),
                g.name(),
                g.members().size()
            ))
            .collect(Collectors.toList());
    }
}
