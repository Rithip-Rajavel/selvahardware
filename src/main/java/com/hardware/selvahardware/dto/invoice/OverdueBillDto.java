package com.hardware.selvahardware.dto.invoice;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for overdue bill information
 */
@Data
@Builder
public class OverdueBillDto {
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private String customerType;

    // Invoice amounts
    private BigDecimal grandTotal;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private BigDecimal totalDue;

    // Dates
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private Integer daysOverdue;

    // Status
    private String invoiceStatus;
    private boolean isOverdue;
    private BigDecimal overdueAmount;

    // Last activity
    private LocalDateTime lastPaymentDate;
    private BigDecimal lastPaymentAmount;

    // Contractor info if applicable
    private UUID contractorId;
    private String contractorName;

    // Overdue summary
    private int totalOverdueInvoicesForCustomer;
    private BigDecimal totalOverdueAmountForCustomer;
}
