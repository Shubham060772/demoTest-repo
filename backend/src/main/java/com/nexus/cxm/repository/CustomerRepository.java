package com.nexus.cxm.repository;

import com.nexus.cxm.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:search IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:segment IS NULL OR c.segment = :segment) AND " +
           "(:company IS NULL OR LOWER(c.company) LIKE LOWER(CONCAT('%', :company, '%')))")
    List<Customer> findWithFilters(
            @Param("search") String search,
            @Param("segment") String segment,
            @Param("company") String company
    );

    @Query("SELECT COUNT(c) FROM Customer c")
    long countAll();
}
