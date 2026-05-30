package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.MessageFilterInput;
import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.service.MessageService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves message root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field    Resolver
 *   ──────────────────────────────────────────────────────────────
 *   GetMessages                →   messages   →  @GraphQLQuery(name = "messages")
 */
@Service
@RequiredArgsConstructor
public class MessageQueryResolver {

    private final MessageService messageService;

    // ── Frontend operation: GetMessages ──────────────────────────────────────
    // query GetMessages($first: Int, $after: String, $filter: MessageFilterInput) {
    //   messages(first: $first, after: $after, filter: $filter) { ... }
    // }
    @GraphQLQuery(name = "messages")
    public Connection<Message> messages(
            @GraphQLArgument(name = "first") Integer first,
            @GraphQLArgument(name = "after") String after,
            @GraphQLArgument(name = "filter") MessageFilterInput filter) {
        return messageService.getMessages(first, after, filter);
    }
}
