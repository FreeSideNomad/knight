package com.knight.contexts.users.users.app.service;

import com.knight.contexts.users.users.api.commands.UserGroupCommands;
import com.knight.contexts.users.users.api.events.*;
import com.knight.contexts.users.users.app.repository.UserRepository;
import com.knight.contexts.users.users.app.repository.UserGroupRepository;
import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.contexts.users.users.domain.aggregate.UserGroup;
import com.knight.platform.sharedkernel.UserGroupId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementing commands for User Group Management.
 * Orchestrates domain operations, repository access, and event publishing.
 */
@Service
public class UserGroupApplicationService implements UserGroupCommands {

    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserGroupApplicationService(UserGroupRepository groupRepository,
                                      UserRepository userRepository,
                                      ApplicationEventPublisher eventPublisher) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public UserGroupId createUserGroup(CreateUserGroupCmd cmd) {
        // Generate new group ID
        UserGroupId groupId = UserGroupId.generate();

        // Create aggregate
        UserGroup group = UserGroup.create(groupId, cmd.profileId(), cmd.name(), cmd.description());

        // Save
        groupRepository.save(group);

        // Publish event
        UserGroupCreated event = new UserGroupCreated(
            groupId,
            cmd.profileId(),
            cmd.name(),
            cmd.description(),
            group.createdAt()
        );
        eventPublisher.publishEvent(event);

        return groupId;
    }

    @Override
    @Transactional
    public void addMemberToGroup(AddMemberCmd cmd) {
        UserGroup group = groupRepository.findById(cmd.groupId())
            .orElseThrow(() -> new IllegalArgumentException("UserGroup not found: " + cmd.groupId()));

        User user = userRepository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId()));

        // Add member (validates profile match)
        group.addMember(user.userId(), user.profileId());

        groupRepository.save(group);

        // Publish event
        UserAddedToGroup event = new UserAddedToGroup(
            cmd.groupId(),
            cmd.userId(),
            group.updatedAt()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public void removeMemberFromGroup(RemoveMemberCmd cmd) {
        UserGroup group = groupRepository.findById(cmd.groupId())
            .orElseThrow(() -> new IllegalArgumentException("UserGroup not found: " + cmd.groupId()));

        group.removeMember(cmd.userId());

        groupRepository.save(group);

        // Publish event
        UserRemovedFromGroup event = new UserRemovedFromGroup(
            cmd.groupId(),
            cmd.userId(),
            group.updatedAt()
        );
        eventPublisher.publishEvent(event);
    }
}
