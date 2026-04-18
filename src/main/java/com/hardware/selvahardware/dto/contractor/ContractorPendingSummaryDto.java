package com.hardware.selvahardware.dto.contractor;

import com.hardware.selvahardware.dto.invoice.InvoiceDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing contractor's own pending bills (when contractor is also a customer)
 */
@Data
@Builder
public class ContractorPendingSummaryDto {
    private List<InvoiceDto> pendingInvoices;
    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private long pendingInvoiceCount;
}
