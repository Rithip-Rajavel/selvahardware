package com.hardware.selvahardware.dto.contractor;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing complete pending bills information for a contractor
 * including all linked customers and their pending bills
 */
@Data
@Builder
public class ContractorPendingBillsDto {
    // Contractor details
    private UUID contractorId;
    private String contractorName;
    private String contractorPhone;
    private String contractorEmail;
    private String contractorAddress;
    private String contractorCity;
    private Boolean isActive;
    private LocalDateTime lastPaymentDate;
    private LocalDateTime lastInvoiceDate;

    // Linked customers with their pending bills
    private List<CustomerPendingSummaryDto> linkedCustomers;

    // Contractor's own pending bills (if any)
    private ContractorPendingSummaryDto ownPendingBills;

    // Grand totals across all linked customers
    private BigDecimal grandTotalBilled;
    private BigDecimal grandTotalPaid;
    private BigDecimal grandTotalPending;
    private long totalPendingInvoiceCount;
    private long totalLinkedCustomers;
    private long customersWithPendingBills;
}
