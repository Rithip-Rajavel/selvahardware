package com.hardware.selvahardware.entity;

import com.hardware.selvahardware.enums.AuditActionType;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Complete audit log for each invoice - tracks all transactions, payments,
 * returns, status changes, and overdue payments.
 */
@Entity
@Table(name = "invoice_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceAuditLog {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "invoice_id", length = 50, nullable = false)
    private UUID invoiceId;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @Column(name = "customer_name")
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private AuditActionType actionType;

    @Column(name = "action_description", columnDefinition = "TEXT")
    private String actionDescription;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", precision = 10, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "amount_paid_before", precision = 10, scale = 2)
    private BigDecimal amountPaidBefore;

    @Column(name = "amount_paid_after", precision = 10, scale = 2)
    private BigDecimal amountPaidAfter;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_id", length = 50)
    private UUID transactionId;

    @Column(name = "return_id", length = 50)
    private UUID returnId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
