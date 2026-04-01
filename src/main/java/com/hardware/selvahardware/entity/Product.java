package com.hardware.selvahardware.entity;

import com.hardware.selvahardware.enums.PricingType;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(length = 50, unique = true, nullable = false, columnDefinition = "varchar(50)")
    private String code;

    @Column(length = 50, columnDefinition = "varchar(50)")
    private String barcode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // This will be the selling price

    @Column(name = "original_amount", precision = 10, scale = 2)
    private BigDecimal originalAmount; // Cost price

    @Column(columnDefinition = "INT DEFAULT 0")
    private int stock;

    @Column(length = 20, nullable = false)
    private String unit;

    @Column(name = "gst_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal gstPercentage;

    @Column(length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type")
    @Builder.Default
    private PricingType pricingType = PricingType.standard;

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
