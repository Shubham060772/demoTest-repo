package com.nexus.cxm.resolver.mutation;

import com.nexus.cxm.model.dto.input.CreateCampaignInput;
import com.nexus.cxm.model.dto.input.UpdateCampaignInput;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CampaignMutationResolver {

    private final CampaignService campaignService;

    @MutationMapping
    public Campaign createCampaign(@Argument CreateCampaignInput input) {
        return campaignService.createCampaign(input);
    }

    @MutationMapping
    public Campaign updateCampaign(@Argument Long id, @Argument UpdateCampaignInput input) {
        return campaignService.updateCampaign(id, input);
    }
}
