package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.repository.MessageRepository;
import com.nexus.cxm.service.CampaignService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resolves nested fields on the Customer type.
 *
 * Contract mapping:
 *   Parent Type   Field       Resolver
 *   ───────────────────────────────────────────────────────────────────
 *   Customer   →  messages  → @GraphQLQuery(name = "messages") with @GraphQLContext Customer
 *   Customer   →  campaigns → @GraphQLQuery(name = "campaigns") with @GraphQLContext Customer
 */
@Service
@RequiredArgsConstructor
public class CustomerFieldResolver {

    private final MessageRepository messageRepository;
    private final CampaignService campaignService;

    @GraphQLQuery(name = "messages")
    public Connection<Message> messages(
            @GraphQLContext Customer customer,
            @GraphQLArgument(name = "first") Integer first,
            @GraphQLArgument(name = "after") String after) {
        List<Message> messages = messageRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
        return Connection.of(messages, first != null ? first : 10, after);
    }

    @GraphQLQuery(name = "campaigns")
    public List<Campaign> campaigns(@GraphQLContext Customer customer) {
        // Customers participate in campaigns via messages; return empty for now
        return List.of();
    }
}
