package com.nexus.cxm.service;

import com.nexus.cxm.exception.ResourceNotFoundException;
import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

    private final ChannelRepository channelRepository;

    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    public Channel getChannelById(Long id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Channel", id));
    }

    public List<Channel> getChannelsByIds(List<Long> ids) {
        return channelRepository.findAllById(ids);
    }

    public long countChannels() {
        return channelRepository.count();
    }
}
