package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.entity.Team;
import com.nexus.cxm.model.entity.User;
import com.nexus.cxm.service.FeatureFlagService;
import com.nexus.cxm.service.PermissionService;
import com.nexus.cxm.service.TeamService;
import com.nexus.cxm.service.UserService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resolves team and user root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field   Resolver
 *   ─────────────────────────────────────────────────────────────
 *   GetTeams                   →   teams     →  @GraphQLQuery(name = "teams")
 *   GetTeam                    →   team      →  @GraphQLQuery(name = "team")
 *   GetUsers                   →   users     →  @GraphQLQuery(name = "users")
 */
@Service
@RequiredArgsConstructor
public class TeamQueryResolver {

    private final TeamService teamService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final FeatureFlagService featureFlagService;

    // ── Frontend operation: GetTeams ──────────────────────────────────────────
    // query GetTeams {
    //   teams { ... }
    // }
    @GraphQLQuery(name = "teams")
    public List<Team> teams() {
        permissionService.require("p:user_manage");
        return teamService.getAllTeams();
    }

    // ── Frontend operation: GetTeam ───────────────────────────────────────────
    // query GetTeam($id: ID!) {
    //   team(id: $id) { ... }
    // }
    @GraphQLQuery(name = "team")
    public Team team(@GraphQLArgument(name = "id") Long id) {
        permissionService.require("p:user_manage");
        return teamService.getTeamById(id);
    }

    // ── Frontend operation: GetUsers ──────────────────────────────────────────
    // query GetUsers {
    //   users { ... }
    // }
    @GraphQLQuery(name = "users")
    public List<User> users() {
        permissionService.require("p:user_manage");
        // USER_PROFILE_V2 flag enables extended user profile fields
        boolean profileV2 = featureFlagService.isOn("USER_PROFILE_V2");
        return userService.getAllUsers();
    }
}
