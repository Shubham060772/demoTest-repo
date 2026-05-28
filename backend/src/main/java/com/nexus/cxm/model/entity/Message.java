package com.nexus.cxm.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageStatus status = MessageStatus.UNREAD;

    @Enumerated(EnumType.STRING)
    private SentimentType sentiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Builder.Default
    private Boolean isInbound = true;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    private OffsetDateTime readAt;
    private OffsetDateTime repliedAt;

    public enum MessageStatus {
        UNREAD, READ, REPLIED, ARCHIVED
    }

    public enum SentimentType {
        POSITIVE, NEUTRAL, NEGATIVE
    }
}
