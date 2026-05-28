package com.nexus.cxm.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.DRAFT;

    private Double budget;

    @Builder.Default
    private Double spentBudget = 0.0;

    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String targetAudience;

    @Builder.Default
    private Double engagementRate = 0.0;

    @Builder.Default
    private Integer impressions = 0;

    @Builder.Default
    private Integer clicks = 0;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CampaignChannel> campaignChannels = new ArrayList<>();

    public enum CampaignStatus {
        DRAFT, SCHEDULED, ACTIVE, PAUSED, COMPLETED, CANCELLED
    }
}
