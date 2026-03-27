package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.PriceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceMappingRepository extends JpaRepository<PriceMapping, UUID> {
    List<PriceMapping> findByCustomerId(UUID customerId);
    Optional<PriceMapping> findByCustomerIdAndProductId(UUID customerId, UUID productId);
    boolean existsByCustomerIdAndProductId(UUID customerId, UUID productId);
}
