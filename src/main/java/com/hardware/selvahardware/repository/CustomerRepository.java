package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.enums.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByType(CustomerType type);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:type IS NULL OR c.type = :type) AND " +
           "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR c.phone LIKE CONCAT('%', :search, '%'))")
    List<Customer> searchCustomers(@Param("type") CustomerType type, @Param("search") String search);
}
