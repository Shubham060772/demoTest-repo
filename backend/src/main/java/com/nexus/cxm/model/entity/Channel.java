package com.nexus.cxm.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Column(nullable = false)
    private String handle;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer followerCount = 0;

    private String iconUrl;
    private String color;

    @CreationTimestamp
    private OffsetDateTime connectedAt;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EngagementMetric> metrics = new ArrayList<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    public enum ChannelType {
        TWITTER, INSTAGRAM, FACEBOOK, LINKEDIN, YOUTUBE, TIKTOK, EMAIL, SMS, WHATSAPP
    }
}
