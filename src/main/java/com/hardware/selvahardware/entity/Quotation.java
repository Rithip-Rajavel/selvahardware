package com.hardware.selvahardware.entity;

import com.hardware.selvahardware.enums.*;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quotations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "quotation_number", length = 50, unique = true, nullable = false, columnDefinition = "varchar(50)")
    private String quotationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @Column(name = "customer_name", nullable = false, columnDefinition = "varchar(255)")
    private String customerName;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType;

    @Column(name = "end_customer_name")
    private String endCustomerName;

    @Column(precision = 10, scale = 2)
    private BigDecimal commission;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total_discount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(name = "total_gst", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalGst;

    @Column(name = "grand_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private QuotationStatus status = QuotationStatus.pending;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "terms_conditions", columnDefinition = "TEXT")
    private String termsConditions;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<QuotationItem> items;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_to_invoice")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Invoice convertedToInvoice;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == QuotationStatus.converted && convertedAt == null) {
            convertedAt = LocalDateTime.now();
        }
    }
}
