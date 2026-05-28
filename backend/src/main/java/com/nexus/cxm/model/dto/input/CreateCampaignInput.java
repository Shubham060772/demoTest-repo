package com.nexus.cxm.model.dto.input;

import com.nexus.cxm.model.entity.Campaign;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateCampaignInput(
        String name,
        String description,
        Double budget,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        String targetAudience,
        List<Long> channelIds
) {}
