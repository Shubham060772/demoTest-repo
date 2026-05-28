package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.entity.*;
import com.nexus.cxm.repository.*;
import com.nexus.cxm.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AnalyticsQueryResolver {

    private final AnalyticsService analyticsService;
    private final CustomerService customerService;
    private final CampaignService campaignService;
    private final MessageService messageService;
    private final ChannelService channelService;
    private final EngagementMetricRepository engagementMetricRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    @QueryMapping
    public Map<String, Object> analyticsSummary(
            @Argument Long channelId,
            @Argument OffsetDateTime startDate,
            @Argument OffsetDateTime endDate) {

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

        Map<String, Object> sentimentBreakdown = new HashMap<>();
        sentimentBreakdown.put("positive", (int) positive);
        sentimentBreakdown.put("negative", (int) negative);
        sentimentBreakdown.put("neutral", (int) neutral);
        sentimentBreakdown.put("positivePercent", positive * 100.0 / total);
        sentimentBreakdown.put("negativePercent", negative * 100.0 / total);
        sentimentBreakdown.put("neutralPercent", neutral * 100.0 / total);

        // Channel performance
        List<Channel> channels = channelId != null
                ? List.of(channelService.getChannelById(channelId))
                : channelService.getAllChannels();

        List<Map<String, Object>> channelPerformance = channels.stream().map(ch -> {
            List<EngagementMetric> chMetrics = analyticsService.getMetricsByChannel(ch.getId());
            int chImpressions = chMetrics.stream().mapToInt(EngagementMetric::getImpressions).sum();
            int chClicks = chMetrics.stream().mapToInt(EngagementMetric::getClicks).sum();
            double chRate = chMetrics.stream().mapToDouble(EngagementMetric::getEngagementRate).average().orElse(0.0);
            long chMessages = messageRepository.findWithFilters(null, ch.getId(), null, null, null).size();

            Map<String, Object> perf = new HashMap<>();
            perf.put("channel", ch);
            perf.put("impressions", chImpressions);
            perf.put("clicks", chClicks);
            perf.put("engagementRate", chRate);
            perf.put("messageCount", (int) chMessages);
            return perf;
        }).toList();

        // Daily metrics
        List<Map<String, Object>> dailyMetrics = metrics.stream().map(m -> {
            Map<String, Object> day = new HashMap<>();
            day.put("date", m.getDate());
            day.put("impressions", m.getImpressions());
            day.put("clicks", m.getClicks());
            day.put("engagements", m.getLikes() + m.getShares() + m.getComments());
            return day;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("totalImpressions", totalImpressions);
        result.put("totalClicks", totalClicks);
        result.put("totalEngagements", totalEngagements);
        result.put("averageEngagementRate", avgEngagementRate);
        result.put("sentimentBreakdown", sentimentBreakdown);
        result.put("channelPerformance", channelPerformance);
        result.put("dailyMetrics", dailyMetrics);
        return result;
    }

    @QueryMapping
    public Map<String, Object> dashboard() {
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
        List<Map<String, Object>> channelBreakdown = channels.stream().map(ch -> {
            List<EngagementMetric> chMetrics = analyticsService.getMetricsByChannel(ch.getId());
            int chImpressions = chMetrics.stream().mapToInt(EngagementMetric::getImpressions).sum();
            int chClicks = chMetrics.stream().mapToInt(EngagementMetric::getClicks).sum();
            double chRate = chMetrics.stream().mapToDouble(EngagementMetric::getEngagementRate).average().orElse(0.0);
            long chMessages = messageRepository.findWithFilters(null, ch.getId(), null, null, null).size();

            Map<String, Object> perf = new HashMap<>();
            perf.put("channel", ch);
            perf.put("impressions", chImpressions);
            perf.put("clicks", chClicks);
            perf.put("engagementRate", chRate);
            perf.put("messageCount", (int) chMessages);
            return perf;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("totalCustomers", (int) totalCustomers);
        result.put("activeCampaigns", (int) activeCampaigns);
        result.put("totalMessages", (int) totalMessages);
        result.put("unreadMessages", (int) unreadMessages);
        result.put("totalChannels", (int) totalChannels);
        result.put("overallEngagementRate", overallEngagementRate);
        result.put("recentMessages", recentMessages);
        result.put("topCampaigns", topCampaigns);
        result.put("channelBreakdown", channelBreakdown);
        return result;
    }
}
