package com.hardware.selvahardware.entity;

import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gst_bill_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstBillItem {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gst_bill_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GstBill gstBill;

    @Column(name = "item_number", nullable = false)
    private Integer itemNumber;

    @Column(nullable = false)
    private String particulars;

    @Column(name = "hsn_code", nullable = false, length = 20)
    private String hsnCode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
