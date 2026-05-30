package com.nexus.cxm.model.dto.input;

import com.nexus.cxm.model.entity.Campaign;

import java.time.OffsetDateTime;
import java.util.List;

import io.leangen.graphql.annotations.types.GraphQLType;

@GraphQLType(name = "CreateCampaignInput")
public record CreateCampaignInput(
        String name,
        String description,
        Double budget,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        String targetAudience,
        List<Long> channelIds
) {}
