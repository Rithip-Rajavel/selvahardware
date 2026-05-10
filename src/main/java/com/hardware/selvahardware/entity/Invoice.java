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
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "invoice_number", length = 50, unique = true, nullable = false, columnDefinition = "varchar(50)")
    private String invoiceNumber;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer contractor;

    @Column(name = "contractor_name")
    private String contractorName;

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

    @Column(name = "amount_paid", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.pending;

    @Enumerated(EnumType.STRING)
    @Column(name = "bill_type")
    @Builder.Default
    private BillType billType = BillType.final_bill;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "next_pay_date")
    private LocalDateTime nextPayDate;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<InvoiceItem> items;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_gst")
    @Builder.Default
    private Boolean isGst = false;

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
