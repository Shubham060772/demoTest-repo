package com.nexus.cxm.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "engagement_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngagementMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private OffsetDateTime date;

    @Builder.Default
    private Integer impressions = 0;

    @Builder.Default
    private Integer clicks = 0;

    @Builder.Default
    private Integer likes = 0;

    @Builder.Default
    private Integer shares = 0;

    @Builder.Default
    private Integer comments = 0;

    @Builder.Default
    private Double engagementRate = 0.0;

    @Builder.Default
    private Integer reach = 0;
}
