package com.nexus.cxm.model.dto.input;

import com.nexus.cxm.model.entity.Message;

public record MessageFilterInput(
        Message.MessageStatus status,
        Long channelId,
        Long customerId,
        Message.SentimentType sentiment,
        Boolean isInbound
) {}
