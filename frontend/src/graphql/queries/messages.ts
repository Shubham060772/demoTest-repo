import { gql } from "@apollo/client";

export const GET_MESSAGES = gql`
  query GetMessages($first: Int, $after: String, $filter: MessageFilterInput) {
    messages(first: $first, after: $after, filter: $filter) {
      edges {
        node {
          id
          subject
          body
          status
          sentiment
          isInbound
          createdAt
          readAt
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
            email
            avatarUrl
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
