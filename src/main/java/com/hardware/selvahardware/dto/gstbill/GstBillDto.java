package com.hardware.selvahardware.dto.gstbill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstBillDto {

    private UUID id;
    private UUID invoiceId;
    private String customerName;
    private String customerAddress;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal sgstAmount;
    private BigDecimal cgstAmount;
    private BigDecimal igstAmount;
    private BigDecimal grandTotal;
    private String amountInWords;
    private List<GstBillItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
