import { gql } from "@apollo/client";

export const GET_ANALYTICS_SUMMARY = gql`
  query GetAnalyticsSummary($channelId: ID, $startDate: DateTime, $endDate: DateTime) {
    analyticsSummary(channelId: $channelId, startDate: $startDate, endDate: $endDate) {
      totalImpressions
      totalClicks
      totalEngagements
      averageEngagementRate
      sentimentBreakdown {
        positive
        neutral
        negative
        positivePercent
        negativePercent
        neutralPercent
      }
      channelPerformance {
        channel {
          id
          name
          type
          color
        }
        impressions
        clicks
        engagementRate
        messageCount
      }
      dailyMetrics {
        date
        impressions
        clicks
        engagements
      }
    }
  }
`;

export const GET_DASHBOARD = gql`
  query GetDashboard {
    dashboard {
      totalCustomers
      activeCampaigns
      totalMessages
      unreadMessages
      totalChannels
      overallEngagementRate
      recentMessages {
        id
        body
        status
        sentiment
        createdAt
        isInbound
        channel {
          id
          name
          type
          color
        }
        customer {
          id
          firstName
          lastName
          avatarUrl
        }
      }
      topCampaigns {
        id
        name
        status
        engagementRate
        impressions
        clicks
      }
      channelBreakdown {
        channel {
          id
          name
          type
          color
        }
        impressions
        clicks
        engagementRate
        messageCount
      }
    }
  }
`;
