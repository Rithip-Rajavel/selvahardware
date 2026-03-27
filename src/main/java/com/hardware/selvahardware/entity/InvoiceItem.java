package com.hardware.selvahardware.entity;

import com.hardware.selvahardware.enums.PricingType;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Invoice invoice;

    @Column(name = "product_id", length = 50, nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_code", length = 50, nullable = false)
    private String productCode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "last_purchased_price", precision = 10, scale = 2)
    private BigDecimal lastPurchasedPrice;

    @Column(name = "gst_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal gstPercentage;

    @Column(name = "with_tax")
    @Builder.Default
    private boolean withTax = true;

    @Column(name = "gst_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal gstAmount;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "custom_total", precision = 10, scale = 2)
    private BigDecimal customTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type")
    @Builder.Default
    private PricingType pricingType = PricingType.standard;

    @Column(precision = 10, scale = 2)
    private BigDecimal height;

    @Column(precision = 10, scale = 2)
    private BigDecimal width;

    @Column(precision = 10, scale = 2)
    private BigDecimal area;
}
