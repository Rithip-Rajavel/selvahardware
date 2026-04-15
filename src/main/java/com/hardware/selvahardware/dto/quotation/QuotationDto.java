package com.hardware.selvahardware.dto.quotation;

import com.hardware.selvahardware.enums.QuotationStatus;
import com.hardware.selvahardware.enums.CustomerType;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationDto {
    private UUID id;
    private String quotationNumber;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private CustomerType customerType;
    private String endCustomerName;
    private BigDecimal commission;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalGst;
    private BigDecimal grandTotal;
    private LocalDateTime validUntil;
    private QuotationStatus status;
    private String notes;
    private String termsConditions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime convertedAt;
    private UUID convertedToInvoiceId;
    private List<QuotationItemDto> items;
}
