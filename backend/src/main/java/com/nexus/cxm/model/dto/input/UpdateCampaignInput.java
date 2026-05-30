package com.nexus.cxm.model.dto.input;

import com.nexus.cxm.model.entity.Campaign;

import java.time.OffsetDateTime;
import java.util.List;

import io.leangen.graphql.annotations.types.GraphQLType;

@GraphQLType(name = "UpdateCampaignInput")
public record UpdateCampaignInput(
        String name,
        String description,
        Campaign.CampaignStatus status,
        Double budget,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        String targetAudience,
        List<Long> channelIds
) {}
