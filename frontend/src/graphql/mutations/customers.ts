import { gql } from "@apollo/client";

export const CREATE_CUSTOMER = gql`
  mutation CreateCustomer($input: CreateCustomerInput!) {
    createCustomer(input: $input) {
      id
      firstName
      lastName
      email
      phone
      company
      tags
      segment
      createdAt
    }
  }
`;

export const UPDATE_CUSTOMER = gql`
  mutation UpdateCustomer($id: Long!, $input: UpdateCustomerInput!) {
    updateCustomer(id: $id, input: $input) {
      id
      firstName
      lastName
      email
      phone
      company
      tags
      segment
      updatedAt
    }
  }
`;

export const DELETE_CUSTOMER = gql`
  mutation DeleteCustomer($id: Long!) {
    deleteCustomer(id: $id)
  }
`;
