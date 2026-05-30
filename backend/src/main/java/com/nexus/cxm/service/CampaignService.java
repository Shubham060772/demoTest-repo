package com.nexus.cxm.service;

import com.nexus.cxm.exception.ResourceNotFoundException;
import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.CampaignFilterInput;
import com.nexus.cxm.model.dto.input.CreateCampaignInput;
import com.nexus.cxm.model.dto.input.UpdateCampaignInput;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.model.entity.CampaignChannel;
import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.repository.CampaignChannelRepository;
import com.nexus.cxm.repository.CampaignRepository;
import com.nexus.cxm.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignChannelRepository campaignChannelRepository;
    private final ChannelRepository channelRepository;

    public Connection<Campaign> getCampaigns(Integer first, String after, CampaignFilterInput filter) {
        String search = filter != null ? filter.search() : null;
        Campaign.CampaignStatus status = filter != null ? filter.status() : null;
        List<Campaign> campaigns = campaignRepository.findWithFilters(search, status);
        return Connection.of(campaigns, first != null ? first : 10, after);
    }

    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", id));
    }

    public List<Channel> getChannelsForCampaign(Long campaignId) {
        List<Long> channelIds = campaignChannelRepository.findByCampaignId(campaignId)
                .stream()
                .map(cc -> cc.getChannel().getId())
                .collect(Collectors.toList());
        return channelRepository.findAllById(channelIds);
    }

    @Transactional
    public Campaign createCampaign(CreateCampaignInput input) {
        Campaign campaign = Campaign.builder()
                .name(input.name())
                .description(input.description())
                .budget(input.budget())
                .startDate(input.startDate())
                .endDate(input.endDate())
                .targetAudience(input.targetAudience())
                .status(Campaign.CampaignStatus.DRAFT)
                .build();
        campaign = campaignRepository.save(campaign);

        if (input.channelIds() != null && !input.channelIds().isEmpty()) {
            final Campaign savedCampaign = campaign;
            List<Channel> channels = channelRepository.findAllById(input.channelIds());
            List<CampaignChannel> campaignChannels = channels.stream()
                    .map(ch -> CampaignChannel.builder().campaign(savedCampaign).channel(ch).build())
                    .collect(Collectors.toList());
            campaignChannelRepository.saveAll(campaignChannels);
        }
        return campaign;
    }

    @Transactional
    public Campaign updateCampaign(Long id, UpdateCampaignInput input) {
        Campaign campaign = getCampaignById(id);
        if (input.name() != null) campaign.setName(input.name());
        if (input.description() != null) campaign.setDescription(input.description());
        if (input.status() != null) campaign.setStatus(input.status());
        if (input.budget() != null) campaign.setBudget(input.budget());
        if (input.startDate() != null) campaign.setStartDate(input.startDate());
        if (input.endDate() != null) campaign.setEndDate(input.endDate());
        if (input.targetAudience() != null) campaign.setTargetAudience(input.targetAudience());

        if (input.channelIds() != null) {
            campaignChannelRepository.deleteByCampaignId(id);
            List<Channel> channels = channelRepository.findAllById(input.channelIds());
            final Campaign finalCampaign = campaign;
            List<CampaignChannel> campaignChannels = channels.stream()
                    .map(ch -> CampaignChannel.builder().campaign(finalCampaign).channel(ch).build())
                    .collect(Collectors.toList());
            campaignChannelRepository.saveAll(campaignChannels);
        }
        return campaignRepository.save(campaign);
    }

    public long countActiveCampaigns() {
        return campaignRepository.countByStatus(Campaign.CampaignStatus.ACTIVE);
    }

    public List<Campaign> getTopCampaigns(int limit) {
        return campaignRepository.findTopCampaigns(PageRequest.of(0, limit));
    }
}
