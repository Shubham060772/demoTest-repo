package com.nexus.cxm.model.dto.input;

import com.nexus.cxm.model.entity.Campaign;

public record CampaignFilterInput(
        String search,
        Campaign.CampaignStatus status,
        Long channelId
) {}
