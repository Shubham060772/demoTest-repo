import { gql } from "@apollo/client";

export const CAMPAIGN_FRAGMENT = gql`
  fragment CampaignFields on Campaign {
    id
    name
    description
    status
    budget
    spentBudget
    startDate
    endDate
    targetAudience
    engagementRate
    impressions
    clicks
    createdAt
    updatedAt
  }
`;

export const GET_CAMPAIGNS = gql`
  ${CAMPAIGN_FRAGMENT}
  query GetCampaigns($first: Int, $after: String, $filter: CampaignFilterInput) {
    campaigns(first: $first, after: $after, filter: $filter) {
      edges {
        node {
          ...CampaignFields
          channels {
            id
            name
            type
            color
          }
        }
        cursor
      }
      pageInfo {
        hasNextPage
        hasPreviousPage
        startCursor
        endCursor
      }
      totalCount
    }
  }
`;

export const GET_CAMPAIGN = gql`
  ${CAMPAIGN_FRAGMENT}
  query GetCampaign($id: ID!) {
    campaign(id: $id) {
      ...CampaignFields
      channels {
        id
        name
        type
        color
        handle
        followerCount
      }
    }
  }
`;
