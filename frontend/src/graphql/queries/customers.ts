import { gql } from "@apollo/client";

export const CUSTOMER_FRAGMENT = gql`
  fragment CustomerFields on Customer {
    id
    firstName
    lastName
    email
    phone
    company
    tags
    segment
    avatarUrl
    createdAt
    updatedAt
  }
`;

export const GET_CUSTOMERS = gql`
  ${CUSTOMER_FRAGMENT}
  query GetCustomers($first: Int, $after: String, $filter: CustomerFilterInput) {
    customers(first: $first, after: $after, filter: $filter) {
      edges {
        node {
          ...CustomerFields
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

export const GET_CUSTOMER = gql`
  ${CUSTOMER_FRAGMENT}
  query GetCustomer($id: ID!) {
    customer(id: $id) {
      ...CustomerFields
      messages(first: 10) {
        edges {
          node {
            id
            body
            status
            sentiment
            createdAt
            channel {
              id
              name
              type
              color
            }
          }
        }
        totalCount
      }
    }
  }
`;
