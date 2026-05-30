package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.repository.ChannelRepository;
import com.nexus.cxm.repository.CustomerRepository;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves nested fields on the Message type.
 *
 * Message.channel and Message.customer are FetchType.LAZY — accessing them
 * outside an active Hibernate session (which is the case during SPQR serialization)
 * throws LazyInitializationException. These explicit resolvers bypass the lazy proxy
 * entirely by re-fetching from the repository using only the stored foreign-key ID.
 *
 * Contract mapping:
 *   Parent Type   Field      Resolver
 *   ─────────────────────────────────────────────────────────────────────
 *   Message    →  channel  → @GraphQLQuery(name = "channel") with @GraphQLContext Message
 *   Message    →  customer → @GraphQLQuery(name = "customer") with @GraphQLContext Message
 */
@Service
@RequiredArgsConstructor
public class MessageFieldResolver {

    private final ChannelRepository channelRepository;
    private final CustomerRepository customerRepository;

    @GraphQLQuery(name = "channel")
    public Channel channel(@GraphQLContext Message message) {
        if (message.getChannel() == null) return null;
        Long channelId = message.getChannel().getId();
        if (channelId == null) return null;
        return channelRepository.findById(channelId).orElse(null);
    }

    @GraphQLQuery(name = "customer")
    public Customer customer(@GraphQLContext Message message) {
        if (message.getCustomer() == null) return null;
        Long customerId = message.getCustomer().getId();
        if (customerId == null) return null;
        return customerRepository.findById(customerId).orElse(null);
    }
}
