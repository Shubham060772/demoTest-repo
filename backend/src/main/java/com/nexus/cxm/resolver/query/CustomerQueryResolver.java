package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.CustomerFilterInput;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.service.CustomerService;
import com.nexus.cxm.service.FeatureFlagService;
import com.nexus.cxm.service.PermissionService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves customer root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field     Resolver
 *   ───────────────────────────────────────────────────────────────
 *   GetCustomers               →   customers   →  @GraphQLQuery(name = "customers")
 *   GetCustomer                →   customer    →  @GraphQLQuery(name = "customer")
 */
@Service
@RequiredArgsConstructor
public class CustomerQueryResolver {

    private final CustomerService customerService;
    private final PermissionService permissionService;
    private final FeatureFlagService featureFlagService;

    // ── Frontend operation: GetCustomers ─────────────────────────────────────
    // query GetCustomers($first: Int, $after: String, $filter: CustomerFilterInput) {
    //   customers(first: $first, after: $after, filter: $filter) { ... }
    // }
    @GraphQLQuery(name = "customers")
    public Connection<Customer> customers(
            @GraphQLArgument(name = "first") Integer first,
            @GraphQLArgument(name = "after") String after,
            @GraphQLArgument(name = "filter") CustomerFilterInput filter) {
        permissionService.require("p:user_view");
        // NEW_USER_FLOW flag enables enriched customer segmentation data
        boolean newFlow = featureFlagService.isOn("NEW_USER_FLOW");
        if (!newFlow && filter != null && filter.segment() != null) {
            // v1 behaviour: segment filter not yet supported
            filter = null;
        }
        return customerService.getCustomers(first, after, filter);
    }

    // ── Frontend operation: GetCustomer ──────────────────────────────────────
    // query GetCustomer($id: ID!) {
    //   customer(id: $id) { ... }
    // }
    @GraphQLQuery(name = "customer")
    public Customer customer(@GraphQLArgument(name = "id") Long id) {
        permissionService.require("p:user_view");
        return customerService.getCustomerById(id);
    }
}
