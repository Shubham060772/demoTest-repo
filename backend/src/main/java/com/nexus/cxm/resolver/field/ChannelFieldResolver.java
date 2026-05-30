package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.model.entity.EngagementMetric;
import com.nexus.cxm.service.AnalyticsService;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resolves nested fields on the Channel type.
 *
 * Contract mapping:
 *   Parent Type   Field     Resolver
 *   ──────────────────────────────────────────────────────────────────
 *   Channel    →  metrics → @GraphQLQuery(name = "metrics") with @GraphQLContext Channel
 */
@Service
@RequiredArgsConstructor
public class ChannelFieldResolver {

    private final AnalyticsService analyticsService;

    @GraphQLQuery(name = "metrics")
    public List<EngagementMetric> metrics(@GraphQLContext Channel channel) {
        return analyticsService.getMetricsByChannel(channel.getId());
    }
}
