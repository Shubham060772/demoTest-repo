package com.nexus.cxm.service;

import com.nexus.cxm.exception.ResourceNotFoundException;
import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.CreateMessageInput;
import com.nexus.cxm.model.dto.input.MessageFilterInput;
import com.nexus.cxm.model.entity.Channel;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.model.entity.Message;
import com.nexus.cxm.repository.ChannelRepository;
import com.nexus.cxm.repository.CustomerRepository;
import com.nexus.cxm.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final CustomerRepository customerRepository;

    private final Random random = new Random();
    private static final Message.SentimentType[] SENTIMENTS = Message.SentimentType.values();

    public Connection<Message> getMessages(Integer first, String after, MessageFilterInput filter) {
        Message.MessageStatus status = filter != null ? filter.status() : null;
        Long channelId = filter != null ? filter.channelId() : null;
        Long customerId = filter != null ? filter.customerId() : null;
        Message.SentimentType sentiment = filter != null ? filter.sentiment() : null;
        Boolean isInbound = filter != null ? filter.isInbound() : null;

        List<Message> messages = messageRepository.findWithFilters(status, channelId, customerId, sentiment, isInbound);
        return Connection.of(messages, first != null ? first : 10, after);
    }

    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", id));
    }

    @Transactional
    public Message sendMessage(CreateMessageInput input) {
        Channel channel = channelRepository.findById(input.channelId())
                .orElseThrow(() -> new ResourceNotFoundException("Channel", input.channelId()));

        Customer customer = null;
        if (input.customerId() != null) {
            customer = customerRepository.findById(input.customerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", input.customerId()));
        }

        Message message = Message.builder()
                .subject(input.subject())
                .body(input.body())
                .channel(channel)
                .customer(customer)
                .isInbound(input.isInbound() != null ? input.isInbound() : true)
                .sentiment(SENTIMENTS[random.nextInt(SENTIMENTS.length)])
                .build();
        return messageRepository.save(message);
    }

    @Transactional
    public Message markMessageRead(Long id) {
        Message message = getMessageById(id);
        message.setStatus(Message.MessageStatus.READ);
        message.setReadAt(OffsetDateTime.now());
        return messageRepository.save(message);
    }

    public long countMessages() {
        return messageRepository.count();
    }

    public long countUnreadMessages() {
        return messageRepository.countByStatus(Message.MessageStatus.UNREAD);
    }

    public List<Message> getRecentMessages(int limit) {
        return messageRepository.findTop5ByOrderByCreatedAtDesc();
    }
}
