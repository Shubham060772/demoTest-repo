package com.nexus.cxm.resolver.mutation;

import com.nexus.cxm.model.dto.input.CreateMessageInput;
import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.service.MessageService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves message mutation root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field          Resolver
 *   ────────────────────────────────────────────────────────────────────────────
 *   SendMessage                →   sendMessage      →  @GraphQLMutation(name = "sendMessage")
 *   MarkMessageRead            →   markMessageRead  →  @GraphQLMutation(name = "markMessageRead")
 */
@Service
@RequiredArgsConstructor
public class MessageMutationResolver {

    private final MessageService messageService;

    // ── Frontend operation: SendMessage ───────────────────────────────────────
    // mutation SendMessage($input: CreateMessageInput!) {
    //   sendMessage(input: $input) { ... }
    // }
    @GraphQLMutation(name = "sendMessage")
    public Message sendMessage(@GraphQLArgument(name = "input") CreateMessageInput input) {
        return messageService.sendMessage(input);
    }

    // ── Frontend operation: MarkMessageRead ───────────────────────────────────
    // mutation MarkMessageRead($id: ID!) {
    //   markMessageRead(id: $id) { ... }
    // }
    @GraphQLMutation(name = "markMessageRead")
    public Message markMessageRead(@GraphQLArgument(name = "id") Long id) {
        return messageService.markMessageRead(id);
    }
}
