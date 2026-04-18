package com.hardware.selvahardware.dto.invoice;

import com.hardware.selvahardware.enums.ReturnStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for invoice return/written bill
 */
@Data
@Builder
public class InvoiceReturnDto {
    private UUID id;
    private String returnNumber;
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID customerId;
    private String customerName;
    private BigDecimal totalReturnAmount;
    private BigDecimal totalReturnQuantity;
    private ReturnStatus status;
    private BigDecimal refundAmount;
    private String refundMethod;
    private String reason;
    private String processedBy;
    private LocalDateTime processedAt;
    private List<InvoiceReturnItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class InvoiceReturnItemDto {
        private UUID id;
        private UUID invoiceItemId;
        private UUID productId;
        private String productName;
        private String productCode;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal gstPercentage;
        private BigDecimal gstAmount;
        private BigDecimal discount;
        private BigDecimal subtotal;
        private BigDecimal total;
        private String reason;
        private BigDecimal originalInvoiceQuantity;
    }
}
