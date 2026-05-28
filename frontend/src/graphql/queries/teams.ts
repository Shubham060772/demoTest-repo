import { gql } from "@apollo/client";

export const GET_TEAMS = gql`
  query GetTeams {
    teams {
      id
      name
      description
      createdAt
      members {
        id
        firstName
        lastName
        email
        role
        avatarUrl
        isActive
      }
    }
  }
`;
