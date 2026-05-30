package com.nexus.cxm.model.dto;

import com.nexus.cxm.model.entity.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChannelPerformance {
    private Channel channel;
    private int impressions;
    private int clicks;
    private double engagementRate;
    private int messageCount;
}
