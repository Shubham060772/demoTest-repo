package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.MessageFilterInput;
import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageQueryResolver {

    private final MessageService messageService;

    @QueryMapping
    public Connection<Message> messages(
            @Argument Integer first,
            @Argument String after,
            @Argument MessageFilterInput filter) {
        return messageService.getMessages(first, after, filter);
    }
}
