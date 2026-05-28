package com.nexus.cxm.model.dto.input;

import java.util.List;

public record UpdateCustomerInput(
        String firstName,
        String lastName,
        String email,
        String phone,
        String company,
        List<String> tags,
        String segment,
        String avatarUrl
) {}
