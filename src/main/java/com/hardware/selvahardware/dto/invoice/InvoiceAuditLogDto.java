package com.hardware.selvahardware.dto.invoice;

import com.hardware.selvahardware.enums.AuditActionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for invoice audit log entry
 */
@Data
@Builder
public class InvoiceAuditLogDto {
    private UUID id;
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID customerId;
    private String customerName;
    private AuditActionType actionType;
    private String actionDescription;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private BigDecimal amountPaidBefore;
    private BigDecimal amountPaidAfter;
    private String paymentMethod;
    private UUID transactionId;
    private UUID returnId;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
}
