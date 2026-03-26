package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.PriceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceMappingRepository extends JpaRepository<PriceMapping, String> {
    List<PriceMapping> findByCustomerId(String customerId);
    Optional<PriceMapping> findByCustomerIdAndProductId(String customerId, String productId);
    boolean existsByCustomerIdAndProductId(String customerId, String productId);
}
