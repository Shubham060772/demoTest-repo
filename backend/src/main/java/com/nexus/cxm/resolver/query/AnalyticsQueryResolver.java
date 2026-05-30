package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.dto.*;
import com.nexus.cxm.model.entity.*;
import com.nexus.cxm.repository.*;
import com.nexus.cxm.service.*;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Resolves analytics root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field           Resolver
 *   ─────────────────────────────────────────────────────────────────────────
 *   GetAnalyticsSummary        →   analyticsSummary  →  @GraphQLQuery(name = "analyticsSummary")
 *   GetDashboard               →   dashboard         →  @GraphQLQuery(name = "dashboard")
 */
@Service
@RequiredArgsConstructor
public class AnalyticsQueryResolver {

    private final AnalyticsService analyticsService;
    private final CustomerService customerService;
    private final CampaignService campaignService;
    private final MessageService messageService;
    private final ChannelService channelService;
    private final EngagementMetricRepository engagementMetricRepository;
    private final MessageRepository messageRepository;

    // ── Frontend operation: GetAnalyticsSummary ──────────────────────────────
    // query GetAnalyticsSummary($channelId: ID, $startDate: DateTime, $endDate: DateTime) {
    //   analyticsSummary(channelId: $channelId, startDate: $startDate, endDate: $endDate) { ... }
    // }
    @GraphQLQuery(name = "analyticsSummary")
    public AnalyticsSummary analyticsSummary(
            @GraphQLArgument(name = "channelId") Long channelId,
            @GraphQLArgument(name = "startDate") OffsetDateTime startDate,
            @GraphQLArgument(name = "endDate") OffsetDateTime endDate) {

        List<EngagementMetric> metrics = analyticsService.getMetrics(channelId, startDate, endDate);

        int totalImpressions = metrics.stream().mapToInt(EngagementMetric::getImpressions).sum();
        int totalClicks = metrics.stream().mapToInt(EngagementMetric::getClicks).sum();
        int totalEngagements = metrics.stream()
                .mapToInt(m -> m.getLikes() + m.getShares() + m.getComments()).sum();
        double avgEngagementRate = metrics.stream()
                .mapToDouble(EngagementMetric::getEngagementRate).average().orElse(0.0);

        // Sentiment breakdown from messages
        List<Message> messages = messageRepository.findWithFilters(null, channelId, null, null, null);
        long positive = messages.stream().filter(m -> m.getSentiment() == Message.SentimentType.POSITIVE).count();
        long negative = messages.stream().filter(m -> m.getSentiment() == Message.SentimentType.NEGATIVE).count();
        long neutral = messages.stream().filter(m -> m.getSentiment() == Message.SentimentType.NEUTRAL).count();
        long total = Math.max(messages.size(), 1);

        SentimentBreakdown sentimentBreakdown = SentimentBreakdown.builder()
                .positive((int) positive)
                .negative((int) negative)
                .neutral((int) neutral)
                .positivePercent(positive * 100.0 / total)
                .negativePercent(negative * 100.0 / total)
                .neutralPercent(neutral * 100.0 / total)
                .build();

        // Channel performance
        List<Channel> channels = channelId != null
                ? List.of(channelService.getChannelById(channelId))
                : channelService.getAllChannels();

        List<ChannelPerformance> channelPerformance = channels.stream().map(ch -> {
            List<EngagementMetric> chMetrics = analyticsService.getMetricsByChannel(ch.getId());
            int chImpressions = chMetrics.stream().mapToInt(EngagementMetric::getImpressions).sum();
            int chClicks = chMetrics.stream().mapToInt(EngagementMetric::getClicks).sum();
            double chRate = chMetrics.stream().mapToDouble(EngagementMetric::getEngagementRate).average().orElse(0.0);
            long chMessages = messageRepository.findWithFilters(null, ch.getId(), null, null, null).size();

            return ChannelPerformance.builder()
                    .channel(ch)
                    .impressions(chImpressions)
                    .clicks(chClicks)
                    .engagementRate(chRate)
                    .messageCount((int) chMessages)
                    .build();
        }).toList();

        // Daily metrics
        List<DailyMetric> dailyMetrics = metrics.stream().map(m -> {
            return DailyMetric.builder()
                    .date(m.getDate())
                    .impressions(m.getImpressions())
                    .clicks(m.getClicks())
                    .engagements(m.getLikes() + m.getShares() + m.getComments())
                    .build();
        }).toList();

        return AnalyticsSummary.builder()
                .totalImpressions(totalImpressions)
                .totalClicks(totalClicks)
                .totalEngagements(totalEngagements)
                .averageEngagementRate(avgEngagementRate)
                .sentimentBreakdown(sentimentBreakdown)
                .channelPerformance(channelPerformance)
                .dailyMetrics(dailyMetrics)
                .build();
    }

    // ── Frontend operation: GetDashboard ─────────────────────────────────────
    // query GetDashboard {
    //   dashboard { ... }
    // }
    @GraphQLQuery(name = "dashboard")
    public Dashboard dashboard() {
        long totalCustomers = customerService.countCustomers();
        long activeCampaigns = campaignService.countActiveCampaigns();
        long totalMessages = messageService.countMessages();
        long unreadMessages = messageService.countUnreadMessages();
        long totalChannels = channelService.countChannels();

        List<EngagementMetric> allMetrics = engagementMetricRepository.findAll();
        double overallEngagementRate = allMetrics.stream()
                .mapToDouble(EngagementMetric::getEngagementRate).average().orElse(0.0);

        List<Message> recentMessages = messageService.getRecentMessages(5);
        List<Campaign> topCampaigns = campaignService.getTopCampaigns(5);

        List<Channel> channels = channelService.getAllChannels();
        List<ChannelPerformance> channelBreakdown = channels.stream().map(ch -> {
            List<EngagementMetric> chMetrics = analyticsService.getMetricsByChannel(ch.getId());
            int chImpressions = chMetrics.stream().mapToInt(EngagementMetric::getImpressions).sum();
            int chClicks = chMetrics.stream().mapToInt(EngagementMetric::getClicks).sum();
            double chRate = chMetrics.stream().mapToDouble(EngagementMetric::getEngagementRate).average().orElse(0.0);
            long chMessages = messageRepository.findWithFilters(null, ch.getId(), null, null, null).size();

            return ChannelPerformance.builder()
                    .channel(ch)
                    .impressions(chImpressions)
                    .clicks(chClicks)
                    .engagementRate(chRate)
                    .messageCount((int) chMessages)
                    .build();
        }).toList();

        return Dashboard.builder()
                .totalCustomers((int) totalCustomers)
                .activeCampaigns((int) activeCampaigns)
                .totalMessages((int) totalMessages)
                .unreadMessages((int) unreadMessages)
                .totalChannels((int) totalChannels)
                .overallEngagementRate(overallEngagementRate)
                .recentMessages(recentMessages)
                .topCampaigns(topCampaigns)
                .channelBreakdown(channelBreakdown)
                .build();
    }
}

