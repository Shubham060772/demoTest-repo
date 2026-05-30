package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.CampaignFilterInput;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.service.CampaignService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves campaign root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field    Resolver
 *   ────────────────────────────────────────────────────────────────
 *   GetCampaigns               →   campaigns  →  @GraphQLQuery(name = "campaigns")
 *   GetCampaign                →   campaign   →  @GraphQLQuery(name = "campaign")
 */
@Service
@RequiredArgsConstructor
public class CampaignQueryResolver {

    private final CampaignService campaignService;

    // ── Frontend operation: GetCampaigns ─────────────────────────────────────
    // query GetCampaigns($first: Int, $after: String, $filter: CampaignFilterInput) {
    //   campaigns(first: $first, after: $after, filter: $filter) { ... }
    // }
    @GraphQLQuery(name = "campaigns")
    public Connection<Campaign> campaigns(
            @GraphQLArgument(name = "first") Integer first,
            @GraphQLArgument(name = "after") String after,
            @GraphQLArgument(name = "filter") CampaignFilterInput filter) {
        return campaignService.getCampaigns(first, after, filter);
    }

    // ── Frontend operation: GetCampaign ──────────────────────────────────────
    // query GetCampaign($id: ID!) {
    //   campaign(id: $id) { ... }
    // }
    @GraphQLQuery(name = "campaign")
    public Campaign campaign(@GraphQLArgument(name = "id") Long id) {
        return campaignService.getCampaignById(id);
    }
}
