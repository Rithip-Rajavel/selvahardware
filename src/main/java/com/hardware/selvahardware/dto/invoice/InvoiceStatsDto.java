package com.hardware.selvahardware.dto.invoice;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InvoiceStatsDto {
    private long totalInvoices;
    private long todayInvoices;
    private BigDecimal todayRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal pendingAmount;
}
