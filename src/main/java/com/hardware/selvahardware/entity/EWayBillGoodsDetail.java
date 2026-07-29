package com.hardware.selvahardware.entity;

import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "eway_bill_goods_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EWayBillGoodsDetail {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eway_bill_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EWayBill ewayBill;

    @Column(name = "product_id", length = 50)
    private String productId;

    @Column(name = "hsn_code", length = 10)
    @Builder.Default
    private String hsnCode = "4412";

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "taxable_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxableAmount;

    @Column(name = "tax_rate", length = 50)
    @Builder.Default
    private String taxRate = "9.000+9.000+NE+NE+0.00";

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
