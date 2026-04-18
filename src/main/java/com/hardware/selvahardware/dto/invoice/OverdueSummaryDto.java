package com.hardware.selvahardware.dto.invoice;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Summary DTO for overdue bills dashboard
 */
@Data
@Builder
public class OverdueSummaryDto {
    private long totalOverdueInvoices;
    private BigDecimal totalOverdueAmount;
    private long totalCustomersWithOverdue;
    private long totalContractorsWithOverdue;

    // Aging buckets
    private long invoicesOverdue1To30Days;
    private long invoicesOverdue31To60Days;
    private long invoicesOverdue61To90Days;
    private long invoicesOverdueMoreThan90Days;

    private BigDecimal amountOverdue1To30Days;
    private BigDecimal amountOverdue31To60Days;
    private BigDecimal amountOverdue61To90Days;
    private BigDecimal amountOverdueMoreThan90Days;

    // Top overdue invoices
    private List<OverdueBillDto> topOverdueInvoices;

    // Customers with most overdue
    private List<CustomerOverdueSummaryDto> customersWithMostOverdue;

    @Data
    @Builder
    public static class CustomerOverdueSummaryDto {
        private String customerId;
        private String customerName;
        private String customerPhone;
        private long overdueInvoiceCount;
        private BigDecimal totalOverdueAmount;
        private int maxDaysOverdue;
    }
}
