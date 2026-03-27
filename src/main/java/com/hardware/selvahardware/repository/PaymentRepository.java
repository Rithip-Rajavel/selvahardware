package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Payment;
import com.hardware.selvahardware.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
