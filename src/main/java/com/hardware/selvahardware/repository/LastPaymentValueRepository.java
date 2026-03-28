package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.LastPaymentValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LastPaymentValueRepository extends JpaRepository<LastPaymentValue, UUID> {

    List<LastPaymentValue> findByCustomer_IdOrderByUpdatedAtDesc(UUID customerId);

    Optional<LastPaymentValue> findByCustomer_IdAndProduct_Id(UUID customerId, UUID productId);

    @Query("SELECT lpv FROM LastPaymentValue lpv WHERE lpv.customer.id = :customerId ORDER BY lpv.updatedAt DESC")
    List<LastPaymentValue> findLastPaymentValuesByCustomer(@Param("customerId") UUID customerId);

    @Query("SELECT lpv FROM LastPaymentValue lpv WHERE lpv.customer.id = :customerId ORDER BY lpv.updatedAt DESC LIMIT 1")
    Optional<LastPaymentValue> findMostRecentLastPaymentValueByCustomer(@Param("customerId") UUID customerId);

    @Query("SELECT lpv FROM LastPaymentValue lpv WHERE lpv.product.id = :productId ORDER BY lpv.updatedAt DESC")
    List<LastPaymentValue> findLastPaymentValuesByProduct(@Param("productId") UUID productId);

    @Query("SELECT lpv FROM LastPaymentValue lpv WHERE lpv.customer.id = :customerId AND lpv.productName LIKE %:productName% ORDER BY lpv.updatedAt DESC")
    List<LastPaymentValue> findByCustomerIdAndProductNameContaining(@Param("customerId") UUID customerId, @Param("productName") String productName);

    @Query("SELECT lpv FROM LastPaymentValue lpv WHERE lpv.billDate BETWEEN :startDate AND :endDate ORDER BY lpv.billDate DESC")
    List<LastPaymentValue> findByBillDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    void deleteByCustomer_IdAndProduct_Id(UUID customerId, UUID productId);
}
