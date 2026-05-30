package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.service.ChannelService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resolves channel root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field    Resolver
 *   ──────────────────────────────────────────────────────────────
 *   GetChannels                →   channels   →  @GraphQLQuery(name = "channels")
 *   GetChannel                 →   channel    →  @GraphQLQuery(name = "channel")
 */
@Service
@RequiredArgsConstructor
public class ChannelQueryResolver {

    private final ChannelService channelService;

    // ── Frontend operation: GetChannels ──────────────────────────────────────
    // query GetChannels {
    //   channels { ... }
    // }
    @GraphQLQuery(name = "channels")
    public List<Channel> channels() {
        return channelService.getAllChannels();
    }

    // ── Frontend operation: GetChannel ───────────────────────────────────────
    // query GetChannel($id: ID!) {
    //   channel(id: $id) { ... }
    // }
    @GraphQLQuery(name = "channel")
    public Channel channel(@GraphQLArgument(name = "id") Long id) {
        return channelService.getChannelById(id);
    }
}
