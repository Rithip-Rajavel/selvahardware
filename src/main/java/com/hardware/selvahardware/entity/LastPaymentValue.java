package com.hardware.selvahardware.entity;

import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "last_payment_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LastPaymentValue {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(name = "product_name", nullable = false, columnDefinition = "varchar(255)")
    private String productName;

    @Column(name = "last_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal lastAmount;

    @Column(name = "last_quantity", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal lastQuantity = BigDecimal.ONE;

    @Column(name = "last_unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal lastUnitPrice;

    @Column(name = "last_discount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal lastDiscount = BigDecimal.ZERO;

    @Column(name = "last_gst_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal lastGstRate = BigDecimal.ZERO;

    @Column(name = "bill_date")
    private LocalDateTime billDate;

    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

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
