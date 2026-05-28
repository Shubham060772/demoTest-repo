package com.nexus.cxm.model.dto.connection;

public record PageInfo(
        boolean hasNextPage,
        boolean hasPreviousPage,
        String startCursor,
        String endCursor
) {}
