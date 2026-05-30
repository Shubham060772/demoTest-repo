package com.nexus.cxm.model.dto.input;

import io.leangen.graphql.annotations.types.GraphQLType;

@GraphQLType(name = "CustomerFilterInput")
public record CustomerFilterInput(
        String search,
        String segment,
        String tag,
        String company
) {}
