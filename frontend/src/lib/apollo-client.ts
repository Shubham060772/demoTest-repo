import { ApolloClient, InMemoryCache, HttpLink } from "@apollo/client";

const httpLink = new HttpLink({
  uri: process.env.NEXT_PUBLIC_GRAPHQL_URL || "http://localhost:8080/graphql",
});

export const apolloClient = new ApolloClient({
  link: httpLink,
  cache: new InMemoryCache({
    typePolicies: {
      Query: {
        fields: {
          customers: {
            keyArgs: ["filter"],
            merge(existing, incoming) {
              return incoming;
            },
          },
          campaigns: {
            keyArgs: ["filter"],
            merge(existing, incoming) {
              return incoming;
            },
          },
          messages: {
            keyArgs: ["filter"],
            merge(existing, incoming) {
              return incoming;
            },
          },
        },
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: "cache-and-network",
    },
  },
});
