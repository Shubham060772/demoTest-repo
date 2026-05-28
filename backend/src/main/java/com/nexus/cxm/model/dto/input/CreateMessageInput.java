package com.nexus.cxm.model.dto.input;

public record CreateMessageInput(
        String subject,
        String body,
        Long channelId,
        Long customerId,
        Boolean isInbound
) {}
