package com.knight.contexts.users.users.infra.rest;

import com.knight.contexts.users.users.api.commands.UserGroupCommands;
import com.knight.platform.sharedkernel.UserGroupId;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing command endpoints for User Group Management.
 */
@RestController
@RequestMapping("/commands/user-groups")
public class UserGroupController {

    private final UserGroupCommands commands;

    public UserGroupController(UserGroupCommands commands) {
        this.commands = commands;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateGroupResult> createUserGroup(@RequestBody CreateGroupRequest request) {
        ServicingProfileId profileId = ServicingProfileId.fromUrn(request.profileUrn());

        UserGroupCommands.CreateUserGroupCmd cmd = new UserGroupCommands.CreateUserGroupCmd(
            profileId,
            request.name(),
            request.description()
        );

        UserGroupId groupId = commands.createUserGroup(cmd);

        return ResponseEntity.ok(new CreateGroupResult(groupId.urn()));
    }

    @PostMapping("/add-member")
    public ResponseEntity<Void> addMember(@RequestBody AddMemberRequest request) {
        UserGroupId groupId = UserGroupId.fromUrn(request.groupUrn());
        UserId userId = UserId.fromUrn(request.userUrn());

        UserGroupCommands.AddMemberCmd cmd = new UserGroupCommands.AddMemberCmd(
            groupId,
            userId
        );

        commands.addMemberToGroup(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-member")
    public ResponseEntity<Void> removeMember(@RequestBody RemoveMemberRequest request) {
        UserGroupId groupId = UserGroupId.fromUrn(request.groupUrn());
        UserId userId = UserId.fromUrn(request.userUrn());

        UserGroupCommands.RemoveMemberCmd cmd = new UserGroupCommands.RemoveMemberCmd(
            groupId,
            userId
        );

        commands.removeMemberFromGroup(cmd);

        return ResponseEntity.ok().build();
    }

    record CreateGroupRequest(
        String profileUrn,
        String name,
        String description
    ) {}

    record CreateGroupResult(String groupUrn) {}

    record AddMemberRequest(String groupUrn, String userUrn) {}

    record RemoveMemberRequest(String groupUrn, String userUrn) {}
}
