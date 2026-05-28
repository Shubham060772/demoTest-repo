package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.CampaignFilterInput;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CampaignQueryResolver {

    private final CampaignService campaignService;

    @QueryMapping
    public Connection<Campaign> campaigns(
            @Argument Integer first,
            @Argument String after,
            @Argument CampaignFilterInput filter) {
        return campaignService.getCampaigns(first, after, filter);
    }

    @QueryMapping
    public Campaign campaign(@Argument Long id) {
        return campaignService.getCampaignById(id);
    }
}
