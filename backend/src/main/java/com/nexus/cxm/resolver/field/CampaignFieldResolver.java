package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.service.CampaignService;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resolves nested fields on the Campaign type.
 *
 * In SPQR, nested type fields are resolved via @GraphQLContext — the parent
 * object is injected automatically by the runtime when the field is requested.
 *
 * Contract mapping:
 *   Parent Type   Field      Resolver
 *   ──────────────────────────────────────────────────────────
 *   Campaign   →  channels → @GraphQLQuery(name = "channels") with @GraphQLContext Campaign
 */
@Service
@RequiredArgsConstructor
public class CampaignFieldResolver {

    private final CampaignService campaignService;

    @GraphQLQuery(name = "channels")
    public List<Channel> channels(@GraphQLContext Campaign campaign) {
        return campaignService.getChannelsForCampaign(campaign.getId());
    }
}
