package com.hardware.selvahardware.dto.contractor;

import com.hardware.selvahardware.dto.invoice.InvoiceDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing a customer linked to a contractor with their pending bills summary
 */
@Data
@Builder
public class CustomerPendingSummaryDto {
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private String customerCity;
    
    // Pending bills for this customer
    private List<InvoiceDto> pendingInvoices;
    
    // Summary totals
    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private long pendingInvoiceCount;
}
