package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.model.entity.EngagementMetric;
import com.nexus.cxm.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChannelFieldResolver {

    private final AnalyticsService analyticsService;

    @SchemaMapping(typeName = "Channel", field = "metrics")
    public List<EngagementMetric> metrics(Channel channel) {
        return analyticsService.getMetricsByChannel(channel.getId());
    }
}
