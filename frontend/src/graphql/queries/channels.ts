import { gql } from "@apollo/client";

export const GET_CHANNELS = gql`
  query GetChannels {
    channels {
      id
      name
      type
      handle
      isActive
      followerCount
      color
      connectedAt
    }
  }
`;

export const GET_CHANNEL = gql`
  query GetChannel($id: Long!) {
    channel(id: $id) {
      id
      name
      type
      handle
      isActive
      followerCount
      color
      connectedAt
      metrics {
        id
        date
        impressions
        clicks
        likes
        shares
        comments
        engagementRate
        reach
      }
    }
  }
`;
