package com.knight.contexts.users.users.infra.rest;

import com.knight.contexts.users.users.api.commands.UserCommands;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.UserId;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

/**
 * REST controller for User Management commands.
 */
@Controller("/commands/users")
@ExecuteOn(TaskExecutors.BLOCKING)
public class UserCommandController {

    @Inject
    UserCommands commands;

    @Post("/create")
    public CreateUserResult createUser(@Body CreateUserRequest req) {
        var clientId = ClientId.of(req.clientUrn());
        var userId = commands.createUser(new UserCommands.CreateUserCmd(
            req.email(),
            req.userType(),
            req.identityProvider(),
            clientId
        ));
        return new CreateUserResult(userId.id());
    }

    @Post("/activate")
    public void activateUser(@Body ActivateUserRequest req) {
        var userId = UserId.of(req.userId());
        commands.activateUser(new UserCommands.ActivateUserCmd(userId));
    }

    @Post("/deactivate")
    public void deactivateUser(@Body DeactivateUserRequest req) {
        var userId = UserId.of(req.userId());
        commands.deactivateUser(new UserCommands.DeactivateUserCmd(userId, req.reason()));
    }

    @Post("/lock")
    public void lockUser(@Body LockUserRequest req) {
        var userId = UserId.of(req.userId());
        commands.lockUser(new UserCommands.LockUserCmd(userId, req.reason()));
    }

    @Post("/unlock")
    public void unlockUser(@Body UnlockUserRequest req) {
        var userId = UserId.of(req.userId());
        commands.unlockUser(new UserCommands.UnlockUserCmd(userId));
    }

    public record CreateUserRequest(String email, String userType, String identityProvider, String clientUrn) {}
    public record CreateUserResult(String userId) {}
    public record ActivateUserRequest(String userId) {}
    public record DeactivateUserRequest(String userId, String reason) {}
    public record LockUserRequest(String userId, String reason) {}
    public record UnlockUserRequest(String userId) {}
}
