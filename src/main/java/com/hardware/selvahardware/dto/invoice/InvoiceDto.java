package com.hardware.selvahardware.dto.invoice;

import com.hardware.selvahardware.enums.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InvoiceDto {
    private String id;
    private String invoiceNumber;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private CustomerType customerType;
    private String endCustomerName;
    private BigDecimal commission;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalGst;
    private BigDecimal grandTotal;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private PaymentMethod paymentMethod;
    private InvoiceStatus status;
    private BillType billType;
    private String notes;
    private List<InvoiceItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class InvoiceItemDto {
        private String id;
        private String productId;
        private String productName;
        private String productCode;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private BigDecimal lastPurchasedPrice;
        private BigDecimal gstPercentage;
        private boolean withTax;
        private BigDecimal gstAmount;
        private BigDecimal discount;
        private BigDecimal discountPercentage;
        private BigDecimal subtotal;
        private BigDecimal total;
        private BigDecimal customTotal;
        private PricingType pricingType;
        private BigDecimal height;
        private BigDecimal width;
        private BigDecimal area;
    }
}
