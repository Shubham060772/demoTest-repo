package com.nexus.cxm.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class AnalyticsSummary {
    private int totalImpressions;
    private int totalClicks;
    private int totalEngagements;
    private double averageEngagementRate;
    private SentimentBreakdown sentimentBreakdown;
    private List<ChannelPerformance> channelPerformance;
    private List<DailyMetric> dailyMetrics;
}
