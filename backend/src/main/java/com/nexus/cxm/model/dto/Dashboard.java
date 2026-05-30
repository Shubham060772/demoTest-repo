package com.nexus.cxm.model.dto;

import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.model.entity.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class Dashboard {
    private int totalCustomers;
    private int activeCampaigns;
    private int totalMessages;
    private int unreadMessages;
    private int totalChannels;
    private double overallEngagementRate;
    private List<Message> recentMessages;
    private List<Campaign> topCampaigns;
    private List<ChannelPerformance> channelBreakdown;
}
