package com.nexus.cxm.resolver.mutation;

import com.nexus.cxm.model.dto.input.CreateMessageInput;
import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageMutationResolver {

    private final MessageService messageService;

    @MutationMapping
    public Message sendMessage(@Argument CreateMessageInput input) {
        return messageService.sendMessage(input);
    }

    @MutationMapping
    public Message markMessageRead(@Argument Long id) {
        return messageService.markMessageRead(id);
    }
}
