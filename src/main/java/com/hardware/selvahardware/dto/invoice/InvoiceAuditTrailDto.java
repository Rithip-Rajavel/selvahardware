package com.hardware.selvahardware.dto.invoice;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Complete audit trail DTO for an invoice - includes all transactions,
 * payments, returns, and status changes
 */
@Data
@Builder
public class InvoiceAuditTrailDto {
    // Invoice basic info
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID customerId;
    private String customerName;
    private String customerPhone;

    // Original invoice amounts
    private BigDecimal originalGrandTotal;
    private BigDecimal originalBalance;

    // Current status
    private String currentStatus;
    private BigDecimal currentBalance;
    private BigDecimal totalPaid;
    private BigDecimal totalReturned;

    // Audit summary
    private int totalTransactions;
    private int totalPayments;
    private int totalReturns;
    private int totalOverdueEvents;

    // Complete timeline
    private List<AuditEventDto> timeline;

    // Payment history
    private List<PaymentSummaryDto> paymentHistory;

    // Return history
    private List<ReturnSummaryDto> returnHistory;

    @Data
    @Builder
    public static class AuditEventDto {
        private String eventType;
        private String description;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
        private String notes;
        private String createdBy;
        private String timestamp;
    }

    @Data
    @Builder
    public static class PaymentSummaryDto {
        private UUID paymentId;
        private BigDecimal amount;
        private String paymentMethod;
        private String notes;
        private String timestamp;
    }

    @Data
    @Builder
    public static class ReturnSummaryDto {
        private UUID returnId;
        private String returnNumber;
        private BigDecimal totalReturnAmount;
        private int itemCount;
        private String reason;
        private String status;
        private String timestamp;
    }
}
