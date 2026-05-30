package com.nexus.cxm.model.dto.input;

import io.leangen.graphql.annotations.types.GraphQLType;

@GraphQLType(name = "CreateMessageInput")
public record CreateMessageInput(
        String subject,
        String body,
        Long channelId,
        Long customerId,
        Boolean isInbound
) {}
