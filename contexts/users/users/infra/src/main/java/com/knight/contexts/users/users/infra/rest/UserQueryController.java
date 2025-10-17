package com.knight.contexts.users.users.infra.rest;

import com.knight.contexts.users.users.api.queries.UserQueries;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing query endpoints for User Management.
 */
@RestController
@RequestMapping("/queries/users")
public class UserQueryController {

    private final UserQueries queries;

    public UserQueryController(UserQueries queries) {
        this.queries = queries;
    }

    @GetMapping("/{userUrn}")
    public ResponseEntity<UserQueries.UserDetail> getUser(@PathVariable String userUrn) {
        UserId userId = UserId.fromUrn(userUrn);
        UserQueries.UserDetail user = queries.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile/{profileUrn}")
    public ResponseEntity<List<UserQueries.UserSummary>> listUsersByProfile(@PathVariable String profileUrn) {
        ServicingProfileId profileId = ServicingProfileId.fromUrn(profileUrn);
        List<UserQueries.UserSummary> users = queries.listUsersByProfile(profileId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/profile/{profileUrn}/administrators")
    public ResponseEntity<List<UserQueries.UserSummary>> getAdministrators(@PathVariable String profileUrn) {
        ServicingProfileId profileId = ServicingProfileId.fromUrn(profileUrn);
        List<UserQueries.UserSummary> admins = queries.getAdministratorsForProfile(profileId);
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{userUrn}/groups")
    public ResponseEntity<List<UserQueries.GroupSummary>> getUserGroups(@PathVariable String userUrn) {
        UserId userId = UserId.fromUrn(userUrn);
        List<UserQueries.GroupSummary> groups = queries.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }
}
