package com.nexus.cxm.model.dto.input;

import com.nexus.cxm.model.entity.Campaign;

import io.leangen.graphql.annotations.types.GraphQLType;

@GraphQLType(name = "CampaignFilterInput")
public record CampaignFilterInput(
        String search,
        Campaign.CampaignStatus status,
        Long channelId
) {}
