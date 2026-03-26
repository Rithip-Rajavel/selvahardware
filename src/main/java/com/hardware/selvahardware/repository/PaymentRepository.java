package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Payment;
import com.hardware.selvahardware.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    @Query("SELECT p FROM Payment p WHERE " +
           "(:customerId IS NULL OR p.customer.id = :customerId) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:fromDate IS NULL OR p.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR p.createdAt <= :toDate)")
    List<Payment> filterPayments(@Param("customerId") String customerId,
                                  @Param("type") TransactionType type,
                                  @Param("fromDate") LocalDateTime fromDate,
                                  @Param("toDate") LocalDateTime toDate);
}
