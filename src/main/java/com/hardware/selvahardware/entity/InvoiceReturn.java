package com.hardware.selvahardware.entity;

import com.hardware.selvahardware.enums.ReturnStatus;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Return/Written Bill entity - Handles customer returns
 */
@Entity
@Table(name = "invoice_returns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceReturn {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "return_number", unique = true, nullable = false)
    private String returnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Invoice invoice;

    @Column(name = "invoice_id_ref", length = 50, nullable = false)
    private UUID invoiceId;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "total_return_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalReturnAmount;

    @Column(name = "total_return_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalReturnQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReturnStatus status = ReturnStatus.pending;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_method")
    private String refundMethod;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "processed_by")
    private String processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @OneToMany(mappedBy = "invoiceReturn", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<InvoiceReturnItem> items;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
