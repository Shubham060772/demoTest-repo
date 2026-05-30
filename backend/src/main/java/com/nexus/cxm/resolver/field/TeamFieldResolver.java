package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.entity.Team;
import com.nexus.cxm.model.entity.User;
import com.nexus.cxm.service.UserService;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resolves nested fields on the Team type.
 *
 * Contract mapping:
 *   Parent Type   Field      Resolver
 *   ──────────────────────────────────────────────────────────────────
 *   Team       →  members  → @GraphQLQuery(name = "members") with @GraphQLContext Team
 */
@Service
@RequiredArgsConstructor
public class TeamFieldResolver {

    private final UserService userService;

    @GraphQLQuery(name = "members")
    public List<User> members(@GraphQLContext Team team) {
        return userService.getUsersByTeam(team.getId());
    }
}
