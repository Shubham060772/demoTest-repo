package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.model.entity.User;
import com.nexus.cxm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageFieldResolver {
    // Message fields are directly mapped from entity
    // Additional field resolvers can be added here if needed
}
