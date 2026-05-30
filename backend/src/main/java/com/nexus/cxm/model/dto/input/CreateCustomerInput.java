package com.nexus.cxm.model.dto.input;

import java.util.List;

import io.leangen.graphql.annotations.types.GraphQLType;

@GraphQLType(name = "CreateCustomerInput")
public record CreateCustomerInput(
        String firstName,
        String lastName,
        String email,
        String phone,
        String company,
        List<String> tags,
        String segment,
        String avatarUrl
) {}
