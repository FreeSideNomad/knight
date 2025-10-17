package com.knight.contexts.users.users.infra.rest;

import com.knight.contexts.users.users.api.commands.UserCommands;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing command endpoints for User Management.
 */
@RestController
@RequestMapping("/commands/users")
public class UserCommandController {

    private final UserCommands commands;

    public UserCommandController(UserCommands commands) {
        this.commands = commands;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateUserResult> createUser(@RequestBody CreateUserRequest request) {
        ServicingProfileId profileId = ServicingProfileId.fromUrn(request.profileUrn());

        UserCommands.CreateUserCmd cmd = new UserCommands.CreateUserCmd(
            profileId,
            request.email(),
            request.firstName(),
            request.lastName(),
            request.role(),
            request.source()
        );

        UserId userId = commands.createUser(cmd);

        return ResponseEntity.ok(new CreateUserResult(userId.urn()));
    }

    @PostMapping("/lock")
    public ResponseEntity<Void> lockUser(@RequestBody LockUserRequest request) {
        UserId userId = UserId.fromUrn(request.userUrn());

        UserCommands.LockUserCmd cmd = new UserCommands.LockUserCmd(
            userId,
            request.lockedBy(),
            request.reason()
        );

        commands.lockUser(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/unlock")
    public ResponseEntity<Void> unlockUser(@RequestBody UnlockUserRequest request) {
        UserId userId = UserId.fromUrn(request.userUrn());

        UserCommands.UnlockUserCmd cmd = new UserCommands.UnlockUserCmd(userId);

        commands.unlockUser(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-role")
    public ResponseEntity<Void> updateUserRole(@RequestBody UpdateRoleRequest request) {
        UserId userId = UserId.fromUrn(request.userUrn());

        UserCommands.UpdateUserRoleCmd cmd = new UserCommands.UpdateUserRoleCmd(
            userId,
            request.newRole()
        );

        commands.updateUserRole(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivateUser(@RequestBody DeactivateUserRequest request) {
        UserId userId = UserId.fromUrn(request.userUrn());

        UserCommands.DeactivateUserCmd cmd = new UserCommands.DeactivateUserCmd(userId);

        commands.deactivateUser(cmd);

        return ResponseEntity.ok().build();
    }

    record CreateUserRequest(
        String profileUrn,
        String email,
        String firstName,
        String lastName,
        String role,
        String source
    ) {}

    record CreateUserResult(String userUrn) {}

    record LockUserRequest(String userUrn, String lockedBy, String reason) {}

    record UnlockUserRequest(String userUrn) {}

    record UpdateRoleRequest(String userUrn, String newRole) {}

    record DeactivateUserRequest(String userUrn) {}
}
