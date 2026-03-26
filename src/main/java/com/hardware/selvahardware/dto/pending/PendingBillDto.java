package com.hardware.selvahardware.dto.pending;

import com.hardware.selvahardware.dto.invoice.InvoiceDto;
import com.hardware.selvahardware.enums.CustomerType;
import com.hardware.selvahardware.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PendingBillDto {
    private String id;
    private String customerId;
    private String customerName;
    private CustomerType customerType;
    private PaymentMethod paymentMethod;
    private BigDecimal billDiscount;
    private BigDecimal amountPaid;
    private String endCustomerName;
    private BigDecimal commission;
    private String notes;
    private List<InvoiceDto.InvoiceItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
