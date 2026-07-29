package com.hardware.selvahardware.entity;

import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;

/**
 * Individual items returned from an invoice
 */
@Entity
@Table(name = "invoice_return_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceReturnItem {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private InvoiceReturn invoiceReturn;

    @Column(name = "invoice_item_id", length = 50)
    private UUID invoiceItemId;

    @Column(name = "product_id", length = 50, nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_code", length = 50)
    private String productCode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "gst_percentage", precision = 5, scale = 2)
    private BigDecimal gstPercentage;

    @Column(name = "gst_amount", precision = 10, scale = 2)
    private BigDecimal gstAmount;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "original_invoice_quantity", precision = 10, scale = 2)
    private BigDecimal originalInvoiceQuantity;
}
