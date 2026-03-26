package com.hardware.selvahardware.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_mapping",
    uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "product_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceMapping {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "customer_id", length = 50, nullable = false)
    private String customerId;

    @Column(name = "product_id", length = 50, nullable = false)
    private String productId;

    @Column(name = "last_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal lastPrice;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
