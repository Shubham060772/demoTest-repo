package com.nexus.cxm.resolver.mutation;

import com.nexus.cxm.model.dto.input.CreateCampaignInput;
import com.nexus.cxm.model.dto.input.UpdateCampaignInput;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.service.CampaignService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves campaign mutation root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field        Resolver
 *   ──────────────────────────────────────────────────────────────────────────
 *   CreateCampaign             →   createCampaign →  @GraphQLMutation(name = "createCampaign")
 *   UpdateCampaign             →   updateCampaign →  @GraphQLMutation(name = "updateCampaign")
 */
@Service
@RequiredArgsConstructor
public class CampaignMutationResolver {

    private final CampaignService campaignService;

    // ── Frontend operation: CreateCampaign ────────────────────────────────────
    // mutation CreateCampaign($input: CreateCampaignInput!) {
    //   createCampaign(input: $input) { ... }
    // }
    @GraphQLMutation(name = "createCampaign")
    public Campaign createCampaign(@GraphQLArgument(name = "input") CreateCampaignInput input) {
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
        return campaignService.updateCampaign(id, input);
    }
}
