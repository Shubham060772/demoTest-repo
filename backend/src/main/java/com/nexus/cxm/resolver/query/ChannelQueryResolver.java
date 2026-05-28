package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChannelQueryResolver {

    private final ChannelService channelService;

    @QueryMapping
    public List<Channel> channels() {
        return channelService.getAllChannels();
    }

    @QueryMapping
    public Channel channel(@Argument Long id) {
        return channelService.getChannelById(id);
    }
}
