package com.nexus.cxm.resolver.query;

import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.CustomerFilterInput;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CustomerQueryResolver {

    private final CustomerService customerService;

    @QueryMapping
    public Connection<Customer> customers(
            @Argument Integer first,
            @Argument String after,
            @Argument CustomerFilterInput filter) {
        return customerService.getCustomers(first, after, filter);
    }

    @QueryMapping
    public Customer customer(@Argument Long id) {
        return customerService.getCustomerById(id);
    }
}
