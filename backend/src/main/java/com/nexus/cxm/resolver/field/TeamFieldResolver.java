package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.entity.Team;
import com.nexus.cxm.model.entity.User;
import com.nexus.cxm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeamFieldResolver {

    private final UserService userService;

    @SchemaMapping(typeName = "Team", field = "members")
    public List<User> members(Team team) {
        return userService.getUsersByTeam(team.getId());
    }
}
