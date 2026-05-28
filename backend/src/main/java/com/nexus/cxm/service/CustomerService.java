package com.nexus.cxm.service;

import com.nexus.cxm.exception.BusinessValidationException;
import com.nexus.cxm.exception.ResourceNotFoundException;
import com.nexus.cxm.model.dto.connection.Connection;
import com.nexus.cxm.model.dto.input.CreateCustomerInput;
import com.nexus.cxm.model.dto.input.CustomerFilterInput;
import com.nexus.cxm.model.dto.input.UpdateCustomerInput;
import com.nexus.cxm.model.entity.Customer;
import com.nexus.cxm.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Connection<Customer> getCustomers(Integer first, String after, CustomerFilterInput filter) {
        String search = filter != null ? filter.search() : null;
        String segment = filter != null ? filter.segment() : null;
        String company = filter != null ? filter.company() : null;

        List<Customer> customers = customerRepository.findWithFilters(search, segment, company);
        return Connection.of(customers, first != null ? first : 10, after);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Transactional
    public Customer createCustomer(CreateCustomerInput input) {
        if (customerRepository.findByEmail(input.email()).isPresent()) {
            throw new BusinessValidationException("Customer with email " + input.email() + " already exists");
        }
        Customer customer = Customer.builder()
                .firstName(input.firstName())
                .lastName(input.lastName())
                .email(input.email())
                .phone(input.phone())
                .company(input.company())
                .tags(input.tags() != null ? new ArrayList<>(input.tags()) : new ArrayList<>())
                .segment(input.segment())
                .avatarUrl(input.avatarUrl())
                .build();
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long id, UpdateCustomerInput input) {
        Customer customer = getCustomerById(id);
        if (input.firstName() != null) customer.setFirstName(input.firstName());
        if (input.lastName() != null) customer.setLastName(input.lastName());
        if (input.email() != null) customer.setEmail(input.email());
        if (input.phone() != null) customer.setPhone(input.phone());
        if (input.company() != null) customer.setCompany(input.company());
        if (input.tags() != null) customer.setTags(new ArrayList<>(input.tags()));
        if (input.segment() != null) customer.setSegment(input.segment());
        if (input.avatarUrl() != null) customer.setAvatarUrl(input.avatarUrl());
        return customerRepository.save(customer);
    }

    @Transactional
    public boolean deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
        return true;
    }

    public long countCustomers() {
        return customerRepository.count();
    }
}
