package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.CampaignFilterInput;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.service.CampaignService;
import com.nexus.cxm.service.FeatureFlagService;
import com.nexus.cxm.service.PermissionService;
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
 *
 *   Permission checks:
 *     campaigns()  → requires p:campaign_view
 *     campaign()   → requires p:campaign_view
 *
 *   Feature-flag checks:
 *     campaigns()  → respects CAMPAIGN_DASHBOARD_V2
 */
@Service
@RequiredArgsConstructor
public class CampaignQueryResolver {

    private final CampaignService campaignService;
    private final PermissionService permissionService;
    private final FeatureFlagService featureFlagService;

    // ── Frontend operation: GetCampaigns ─────────────────────────────────────
    // query GetCampaigns($first: Int, $after: String, $filter: CampaignFilterInput) {
    //   campaigns(first: $first, after: $after, filter: $filter) { ... }
    // }
    @GraphQLQuery(name = "campaigns")
    public Connection<Campaign> campaigns(
            @GraphQLArgument(name = "first") Integer first,
            @GraphQLArgument(name = "after") String after,
            @GraphQLArgument(name = "filter") CampaignFilterInput filter) {
        permissionService.require("p:campaign_view");
        // CAMPAIGN_DASHBOARD_V2 enables advanced filtering; degrade gracefully when off
        boolean dashboardV2 = featureFlagService.isOn("CAMPAIGN_DASHBOARD_V2");
        if (!dashboardV2 && filter != null && filter.status() != null) {
            // v1 behaviour: ignore status filter
            filter = null;
        }
        return campaignService.getCampaigns(first, after, filter);
    }

    // ── Frontend operation: GetCampaign ──────────────────────────────────────
    // query GetCampaign($id: ID!) {
    //   campaign(id: $id) { ... }
    // }
    @GraphQLQuery(name = "campaign")
    public Campaign campaign(@GraphQLArgument(name = "id") Long id) {
        permissionService.require("p:campaign_view");
        return campaignService.getCampaignById(id);
    }
}
