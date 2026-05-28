package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CampaignFieldResolver {

    private final CampaignService campaignService;

    @SchemaMapping(typeName = "Campaign", field = "channels")
    public List<Channel> channels(Campaign campaign) {
        return campaignService.getChannelsForCampaign(campaign.getId());
    }
}
