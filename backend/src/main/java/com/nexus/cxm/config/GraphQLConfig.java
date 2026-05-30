package com.nexus.cxm.config;

import com.nexus.cxm.resolver.field.*;
import com.nexus.cxm.resolver.mutation.CampaignMutationResolver;
import com.nexus.cxm.resolver.mutation.CustomerMutationResolver;
import com.nexus.cxm.resolver.mutation.MessageMutationResolver;
import com.nexus.cxm.resolver.query.*;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SPQR GraphQL configuration.
 *
 * Builds the GraphQL schema entirely from @GraphQLQuery / @GraphQLMutation annotations
 * on resolver classes — no .graphqls schema files needed. This is the code-first approach.
 *
 * Contract mapping enforced here:
 *   Operation Name (frontend, client-only) → Root Field → @GraphQLQuery/@GraphQLMutation(name="...")
 *   ─────────────────────────────────────────────────────────────────────────────────────────────────
 *   GetAnalyticsSummary    → analyticsSummary  → @GraphQLQuery(name = "analyticsSummary")
 *   GetDashboard           → dashboard         → @GraphQLQuery(name = "dashboard")
 *   GetCampaigns           → campaigns         → @GraphQLQuery(name = "campaigns")
 *   GetCampaign            → campaign          → @GraphQLQuery(name = "campaign")
 *   GetChannels            → channels          → @GraphQLQuery(name = "channels")
 *   GetChannel             → channel           → @GraphQLQuery(name = "channel")
 *   GetCustomers           → customers         → @GraphQLQuery(name = "customers")
 *   GetCustomer            → customer          → @GraphQLQuery(name = "customer")
 *   GetMessages            → messages          → @GraphQLQuery(name = "messages")
 *   GetTeams               → teams             → @GraphQLQuery(name = "teams")
 *   GetTeam                → team              → @GraphQLQuery(name = "team")
 *   GetUsers               → users             → @GraphQLQuery(name = "users")
 *   CreateCampaign         → createCampaign    → @GraphQLMutation(name = "createCampaign")
 *   UpdateCampaign         → updateCampaign    → @GraphQLMutation(name = "updateCampaign")
 *   CreateCustomer         → createCustomer    → @GraphQLMutation(name = "createCustomer")
 *   UpdateCustomer         → updateCustomer    → @GraphQLMutation(name = "updateCustomer")
 *   DeleteCustomer         → deleteCustomer    → @GraphQLMutation(name = "deleteCustomer")
 *   SendMessage            → sendMessage       → @GraphQLMutation(name = "sendMessage")
 *   MarkMessageRead        → markMessageRead   → @GraphQLMutation(name = "markMessageRead")
 */
@Configuration
public class GraphQLConfig {

    @Bean
    public GraphQLSchema graphQLSchema(
            // Query resolvers
            AnalyticsQueryResolver analyticsQueryResolver,
            CampaignQueryResolver campaignQueryResolver,
            ChannelQueryResolver channelQueryResolver,
            CustomerQueryResolver customerQueryResolver,
            MessageQueryResolver messageQueryResolver,
            TeamQueryResolver teamQueryResolver,
            // Mutation resolvers
            CampaignMutationResolver campaignMutationResolver,
            CustomerMutationResolver customerMutationResolver,
            MessageMutationResolver messageMutationResolver,
            // Nested field resolvers
            CampaignFieldResolver campaignFieldResolver,
            ChannelFieldResolver channelFieldResolver,
            CustomerFieldResolver customerFieldResolver,
            MessageFieldResolver messageFieldResolver,
            TeamFieldResolver teamFieldResolver) {

        return new GraphQLSchemaGenerator()
                .withResolverBuilders(new AnnotatedResolverBuilder())
                .withOperationsFromSingletons(
                        // Root query resolvers — each method carries @GraphQLQuery(name="<rootField>")
                        analyticsQueryResolver,
                        campaignQueryResolver,
                        channelQueryResolver,
                        customerQueryResolver,
                        messageQueryResolver,
                        teamQueryResolver,
                        // Root mutation resolvers — each method carries @GraphQLMutation(name="<rootField>")
                        campaignMutationResolver,
                        customerMutationResolver,
                        messageMutationResolver,
                        // Nested field resolvers — each method carries @GraphQLQuery(name="<field>") + @GraphQLContext
                        campaignFieldResolver,
                        channelFieldResolver,
                        customerFieldResolver,
                        messageFieldResolver,
                        teamFieldResolver
                )
                .withTypeInfoGenerator(new io.leangen.graphql.metadata.strategy.type.DefaultTypeInfoGenerator() {
                    @Override
                    public String generateInputTypeName(java.lang.reflect.AnnotatedType type, io.leangen.graphql.metadata.messages.MessageBundle messageBundle) {
                        String name = super.generateInputTypeName(type, messageBundle);
                        if (name.endsWith("InputInput")) {
                            return name.substring(0, name.length() - 5);
                        }
                        return name;
                    }
                })
                .withValueMapperFactory(new JacksonValueMapperFactory())
                .generate();
    }

    @Bean
    public GraphQL graphQL(GraphQLSchema schema) {
        return GraphQL.newGraphQL(schema).build();
    }
}
