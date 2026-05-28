package com.nexus.cxm.resolver.field;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.entity.Campaign;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.repository.MessageRepository;
import com.nexus.cxm.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerFieldResolver {

    private final MessageRepository messageRepository;
    private final CampaignService campaignService;

    @SchemaMapping(typeName = "Customer", field = "messages")
    public Connection<Message> messages(Customer customer,
                                       @Argument Integer first,
                                       @Argument String after) {
        List<Message> messages = messageRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
        return Connection.of(messages, first != null ? first : 10, after);
    }

    @SchemaMapping(typeName = "Customer", field = "campaigns")
    public List<Campaign> campaigns(Customer customer) {
        // Customers participate in campaigns via messages; return empty for now
        return List.of();
    }
}
