package com.hardware.selvahardware.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commission {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "contractor_id", length = 50, nullable = false)
    private String contractorId;

    @Column(name = "contractor_name", nullable = false)
    private String contractorName;

    @Column(name = "invoice_id", length = 50, nullable = false)
    private String invoiceId;

    @Column(name = "invoice_number", length = 50, nullable = false)
    private String invoiceNumber;

    @Column(name = "end_customer_name", nullable = false)
    private String endCustomerName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
