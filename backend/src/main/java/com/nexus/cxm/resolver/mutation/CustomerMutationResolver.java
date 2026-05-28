package com.nexus.cxm.resolver.mutation;

import com.nexus.cxm.model.dto.input.CreateCustomerInput;
import com.nexus.cxm.model.dto.input.UpdateCustomerInput;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CustomerMutationResolver {

    private final CustomerService customerService;

    @MutationMapping
    public Customer createCustomer(@Argument CreateCustomerInput input) {
        return customerService.createCustomer(input);
    }

    @MutationMapping
    public Customer updateCustomer(@Argument Long id, @Argument UpdateCustomerInput input) {
        return customerService.updateCustomer(id, input);
    }

    @MutationMapping
    public Boolean deleteCustomer(@Argument Long id) {
        return customerService.deleteCustomer(id);
    }
}
