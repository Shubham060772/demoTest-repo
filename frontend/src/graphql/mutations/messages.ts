import { gql } from "@apollo/client";

export const SEND_MESSAGE = gql`
  mutation SendMessage($input: CreateMessageInput!) {
    sendMessage(input: $input) {
      id
      body
      status
      sentiment
      isInbound
      createdAt
      channel {
        id
        name
        type
      }
    }
  }
`;

export const MARK_MESSAGE_READ = gql`
  mutation MarkMessageRead($id: Long!) {
    markMessageRead(id: $id) {
      id
      status
      readAt
    }
  }
`;
