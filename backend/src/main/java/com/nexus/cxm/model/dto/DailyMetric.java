package com.nexus.cxm.model.dto;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DailyMetric {
    private OffsetDateTime date;
    private int impressions;
    private int clicks;
    private int engagements;
}
