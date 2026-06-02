import { gql } from "@apollo/client";

export const CREATE_CAMPAIGN = gql`
  mutation CreateCampaign($input: CreateCampaignInput!) {
    createCampaign(input: $input) {
      id
      name
      description
      status
      budget
      startDate
      endDate
      createdAt
    }
  }
`;

export const UPDATE_CAMPAIGN = gql`
  mutation UpdateCampaign($id: Long!, $input: UpdateCampaignInput!) {
    updateCampaign(id: $id, input: $input) {
      id
      name
      status
      budget
      updatedAt
    }
  }
`;
