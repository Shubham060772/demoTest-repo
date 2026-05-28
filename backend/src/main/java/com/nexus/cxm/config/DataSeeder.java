package com.nexus.cxm.config;

import com.nexus.cxm.model.entity.*;
import com.nexus.cxm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ChannelRepository channelRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignChannelRepository campaignChannelRepository;
    private final MessageRepository messageRepository;
    private final EngagementMetricRepository engagementMetricRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    private final Random random = new Random(42);

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }
        log.info("Seeding database...");
        seedTeams();
        seedChannels();
        seedCustomers();
        seedCampaigns();
        seedMessages();
        seedMetrics();
        log.info("Database seeding complete.");
    }

    private void seedTeams() {
        Team engineering = teamRepository.save(Team.builder().name("Engineering").description("Product & tech team").build());
        Team marketing = teamRepository.save(Team.builder().name("Marketing").description("Brand & growth team").build());
        Team support = teamRepository.save(Team.builder().name("Customer Support").description("Customer success team").build());

        userRepository.saveAll(List.of(
            User.builder().firstName("Alice").lastName("Chen").email("alice@nexus.io").role(User.UserRole.ADMIN).team(engineering).build(),
            User.builder().firstName("Bob").lastName("Martinez").email("bob@nexus.io").role(User.UserRole.MANAGER).team(marketing).build(),
            User.builder().firstName("Carol").lastName("Smith").email("carol@nexus.io").role(User.UserRole.ANALYST).team(marketing).build(),
            User.builder().firstName("David").lastName("Kim").email("david@nexus.io").role(User.UserRole.AGENT).team(support).build(),
            User.builder().firstName("Emma").lastName("Johnson").email("emma@nexus.io").role(User.UserRole.AGENT).team(support).build(),
            User.builder().firstName("Frank").lastName("Williams").email("frank@nexus.io").role(User.UserRole.VIEWER).team(engineering).build()
        ));
    }

    private void seedChannels() {
        channelRepository.saveAll(List.of(
            Channel.builder().name("Twitter / X").type(Channel.ChannelType.TWITTER).handle("@nexuscxm").followerCount(45200).color("#1DA1F2").build(),
            Channel.builder().name("Instagram").type(Channel.ChannelType.INSTAGRAM).handle("@nexus.cxm").followerCount(89100).color("#E1306C").build(),
            Channel.builder().name("Facebook").type(Channel.ChannelType.FACEBOOK).handle("NexusCXM").followerCount(32400).color("#1877F2").build(),
            Channel.builder().name("LinkedIn").type(Channel.ChannelType.LINKEDIN).handle("nexus-cxm").followerCount(15800).color("#0077B5").build(),
            Channel.builder().name("YouTube").type(Channel.ChannelType.YOUTUBE).handle("NexusCXMOfficial").followerCount(8600).color("#FF0000").build(),
            Channel.builder().name("Email").type(Channel.ChannelType.EMAIL).handle("hello@nexus.io").followerCount(0).color("#6366F1").build()
        ));
    }

    private void seedCustomers() {
        String[] segments = {"Enterprise", "SMB", "Startup", "Consumer", "Government"};
        String[] companies = {"Acme Corp", "TechVentures", "GlobalRetail", "DataSystems", "CloudBase", "MediaGroup", "FinServ", "HealthCo"};
        String[][] nameData = {
            {"James", "Wilson"}, {"Sarah", "Davis"}, {"Michael", "Brown"}, {"Jennifer", "Taylor"},
            {"Robert", "Anderson"}, {"Linda", "Thomas"}, {"William", "Jackson"}, {"Barbara", "White"},
            {"David", "Harris"}, {"Susan", "Martin"}, {"Richard", "Garcia"}, {"Jessica", "Martinez"},
            {"Joseph", "Robinson"}, {"Karen", "Clark"}, {"Thomas", "Rodriguez"}, {"Nancy", "Lewis"},
            {"Charles", "Lee"}, {"Betty", "Walker"}, {"Christopher", "Hall"}, {"Margaret", "Allen"},
            {"Daniel", "Young"}, {"Lisa", "Hernandez"}, {"Matthew", "King"}, {"Dorothy", "Wright"},
            {"Anthony", "Lopez"}, {"Sandra", "Hill"}, {"Mark", "Scott"}, {"Ashley", "Green"},
            {"Donald", "Adams"}, {"Emily", "Baker"}
        };

        for (int i = 0; i < nameData.length; i++) {
            String firstName = nameData[i][0];
            String lastName = nameData[i][1];
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com";
            customerRepository.save(Customer.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .phone("+1-555-" + String.format("%04d", 1000 + i))
                    .company(companies[i % companies.length])
                    .segment(segments[i % segments.length])
                    .tags(List.of("vip", "active").subList(0, 1 + (i % 2)))
                    .build());
        }
    }

    private void seedCampaigns() {
        List<Channel> channels = channelRepository.findAll();
        Campaign.CampaignStatus[] statuses = Campaign.CampaignStatus.values();

        String[][] campaignData = {
            {"Summer Launch 2024", "Multi-channel summer product launch campaign"},
            {"Q4 Brand Awareness", "End of year brand awareness push across social"},
            {"Email Nurture Series", "Automated email drip campaign for leads"},
            {"LinkedIn B2B Push", "Targeted LinkedIn outreach for enterprise leads"},
            {"Holiday Promotion", "Festive season promotional campaign"},
            {"Product Announcement", "New feature announcement across all channels"}
        };

        for (int i = 0; i < campaignData.length; i++) {
            Campaign campaign = campaignRepository.save(Campaign.builder()
                    .name(campaignData[i][0])
                    .description(campaignData[i][1])
                    .status(statuses[i % statuses.length])
                    .budget(10000.0 + random.nextInt(90000))
                    .spentBudget((double) random.nextInt(5000))
                    .startDate(OffsetDateTime.now().minusDays(30 - i * 5))
                    .endDate(OffsetDateTime.now().plusDays(30 + i * 10))
                    .targetAudience("18-45 demographics, tech-savvy professionals")
                    .engagementRate(2.5 + random.nextDouble() * 5)
                    .impressions(10000 + random.nextInt(100000))
                    .clicks(500 + random.nextInt(5000))
                    .build());

            // Assign 2-3 random channels
            for (int j = 0; j < Math.min(3, channels.size()); j++) {
                campaignChannelRepository.save(CampaignChannel.builder()
                        .campaign(campaign)
                        .channel(channels.get((i + j) % channels.size()))
                        .build());
            }
        }
    }

    private void seedMessages() {
        List<Channel> channels = channelRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        Message.SentimentType[] sentiments = Message.SentimentType.values();
        Message.MessageStatus[] statuses = Message.MessageStatus.values();

        String[] bodies = {
            "I love your new product features! Keep up the great work.",
            "Having trouble logging in to my account. Can someone help?",
            "The customer service was exceptional. Thank you!",
            "When will the new update be available?",
            "I'm disappointed with the recent changes to the platform.",
            "Just saw your latest post and wanted to reach out!",
            "Can you provide more details about the enterprise plan?",
            "Your product has completely transformed how we work.",
            "I have a billing question I need help with.",
            "Great job on the latest campaign! Very impactful."
        };

        for (int i = 0; i < 40; i++) {
            messageRepository.save(Message.builder()
                    .subject(i % 3 == 0 ? "Re: Your inquiry #" + (1000 + i) : null)
                    .body(bodies[i % bodies.length])
                    .status(statuses[i % statuses.length])
                    .sentiment(sentiments[i % sentiments.length])
                    .channel(channels.get(i % channels.size()))
                    .customer(i % 5 == 0 ? null : customers.get(i % customers.size()))
                    .isInbound(i % 3 != 0)
                    .createdAt(OffsetDateTime.now().minusHours(i * 2))
                    .build());
        }
    }

    private void seedMetrics() {
        List<Channel> channels = channelRepository.findAll();
        for (Channel channel : channels) {
            for (int day = 30; day >= 0; day--) {
                int base = channel.getFollowerCount() / 100 + 1;
                int impressions = base * (100 + random.nextInt(500));
                int clicks = impressions / (5 + random.nextInt(10));
                int likes = impressions / (10 + random.nextInt(20));
                int shares = likes / (2 + random.nextInt(5));
                int comments = likes / (3 + random.nextInt(5));
                int reach = impressions * 2 / 3;
                double engagementRate = (likes + shares + comments) * 100.0 / Math.max(impressions, 1);

                engagementMetricRepository.save(EngagementMetric.builder()
                        .channel(channel)
                        .date(OffsetDateTime.now().minusDays(day))
                        .impressions(impressions)
                        .clicks(clicks)
                        .likes(likes)
                        .shares(shares)
                        .comments(comments)
                        .reach(reach)
                        .engagementRate(engagementRate)
                        .build());
            }
        }
    }
}
