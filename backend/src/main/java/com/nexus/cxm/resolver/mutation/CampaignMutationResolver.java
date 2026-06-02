package com.nexus.cxm.resolver.mutation;

import com.nexus.cxm.model.dto.input.CreateCampaignInput;
import com.nexus.cxm.model.dto.input.UpdateCampaignInput;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.service.CampaignService;
import com.nexus.cxm.service.FeatureFlagService;
import com.nexus.cxm.service.PermissionService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves campaign mutation root fields declared in the GraphQL schema.
 *   Contract mapping:
 *   Operation Name (client-only)   Root Field        Resolver
 *   ──────────────────────────────────────────────────────────────────────────
 *   CreateCampaign             →   createCampaign →  @GraphQLMutation(name = "createCampaign")
 *   UpdateCampaign             →   updateCampaign →  @GraphQLMutation(name = "updateCampaign")
 *
 *   Permission checks:
 *     createCampaign() → requires p:campaign_edit
 *     updateCampaign() → requires p:campaign_edit
 *
 *   Feature-flag checks:
 *     createCampaign() → respects BULK_OPERATIONS (allows bulk channel ids)
 */
@Service
@RequiredArgsConstructor
public class CampaignMutationResolver {

    private final CampaignService campaignService;
    private final PermissionService permissionService;
    private final FeatureFlagService featureFlagService;

    // ── Frontend operation: CreateCampaign ────────────────────────────────────
    // mutation CreateCampaign($input: CreateCampaignInput!) {
    //   createCampaign(input: $input) { ... }
    // }
    @GraphQLMutation(name = "createCampaign")
    public Campaign createCampaign(@GraphQLArgument(name = "input") CreateCampaignInput input) {
        permissionService.require("p:campaign_edit");
        // BULK_OPERATIONS flag gates multi-channel assignment on creation
        boolean bulkOps = featureFlagService.isOn("BULK_OPERATIONS");
        if (!bulkOps && input.channelIds() != null && input.channelIds().size() > 1) {
            throw new com.nexus.cxm.exception.BusinessValidationException(
                    "Bulk channel assignment requires the BULK_OPERATIONS feature to be enabled");
        }
        return campaignService.createCampaign(input);
    }

    // ── Frontend operation: UpdateCampaign ────────────────────────────────────
    // mutation UpdateCampaign($id: ID!, $input: UpdateCampaignInput!) {
    //   updateCampaign(id: $id, input: $input) { ... }
    // }
    @GraphQLMutation(name = "updateCampaign")
    public Campaign updateCampaign(
            @GraphQLArgument(name = "id") Long id,
            @GraphQLArgument(name = "input") UpdateCampaignInput input) {
        permissionService.require("p:campaign_edit");
        return campaignService.updateCampaign(id, input);
    }
}
