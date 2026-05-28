package com.nexus.cxm.service;

import com.nexus.cxm.model.entity.EngagementMetric;
import com.nexus.cxm.repository.EngagementMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final EngagementMetricRepository engagementMetricRepository;

    public List<EngagementMetric> getMetrics(Long channelId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return engagementMetricRepository.findWithFilters(channelId, startDate, endDate);
    }

    public List<EngagementMetric> getMetricsByChannel(Long channelId) {
        return engagementMetricRepository.findByChannelId(channelId);
    }
}
