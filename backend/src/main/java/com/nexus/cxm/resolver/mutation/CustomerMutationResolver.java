package com.nexus.cxm.resolver.mutation;

import com.nexus.cxm.model.dto.input.CreateCustomerInput;
import com.nexus.cxm.model.dto.input.UpdateCustomerInput;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.service.CustomerService;
import com.nexus.cxm.service.FeatureFlagService;
import com.nexus.cxm.service.PermissionService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves customer mutation root fields declared in the GraphQL schema.
 *
 * Contract mapping:
 *   Operation Name (client-only)   Root Field         Resolver
 *   ───────────────────────────────────────────────────────────────────────────
 *   CreateCustomer             →   createCustomer  →  @GraphQLMutation(name = "createCustomer")
 *   UpdateCustomer             →   updateCustomer  →  @GraphQLMutation(name = "updateCustomer")
 *   DeleteCustomer             →   deleteCustomer  →  @GraphQLMutation(name = "deleteCustomer")
 */
@Service
@RequiredArgsConstructor
public class CustomerMutationResolver {

    private final CustomerService customerService;
    private final PermissionService permissionService;
    private final FeatureFlagService featureFlagService;

    // ── Frontend operation: CreateCustomer ────────────────────────────────────
    // mutation CreateCustomer($input: CreateCustomerInput!) {
    //   createCustomer(input: $input) { ... }
    // }
    @GraphQLMutation(name = "createCustomer")
    public Customer createCustomer(@GraphQLArgument(name = "input") CreateCustomerInput input) {
        permissionService.require("p:user_edit");
        // NEW_USER_FLOW enriches the creation with default segment assignment
        boolean newFlow = featureFlagService.isOn("NEW_USER_FLOW");
        if (newFlow && (input.segment() == null || input.segment().isBlank())) {
            // v2 flow auto-assigns default segment – handled in service in real impl
        }
        return customerService.createCustomer(input);
    }

    // ── Frontend operation: UpdateCustomer ────────────────────────────────────
    // mutation UpdateCustomer($id: ID!, $input: UpdateCustomerInput!) {
    //   updateCustomer(id: $id, input: $input) { ... }
    // }
    @GraphQLMutation(name = "updateCustomer")
    public Customer updateCustomer(
            @GraphQLArgument(name = "id") Long id,
            @GraphQLArgument(name = "input") UpdateCustomerInput input) {
        permissionService.require("p:user_edit");
        return customerService.updateCustomer(id, input);
    }

    // ── Frontend operation: DeleteCustomer ────────────────────────────────────
    // mutation DeleteCustomer($id: ID!) {
    //   deleteCustomer(id: $id)
    // }
    @GraphQLMutation(name = "deleteCustomer")
    public Boolean deleteCustomer(@GraphQLArgument(name = "id") Long id) {
        permissionService.require("p:user_delete");
        return customerService.deleteCustomer(id);
    }
}
