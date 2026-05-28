package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.entity.Team;
import com.nexus.cxm.model.entity.User;
import com.nexus.cxm.service.TeamService;
import com.nexus.cxm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeamQueryResolver {

    private final TeamService teamService;
    private final UserService userService;

    @QueryMapping
    public List<Team> teams() {
        return teamService.getAllTeams();
    }

    @QueryMapping
    public Team team(@Argument Long id) {
        return teamService.getTeamById(id);
    }

    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }
}
