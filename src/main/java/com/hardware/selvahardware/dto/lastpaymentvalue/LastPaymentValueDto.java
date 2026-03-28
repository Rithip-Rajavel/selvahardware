package com.hardware.selvahardware.dto.lastpaymentvalue;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LastPaymentValueDto {
    
    private UUID id;
    private UUID customerId;
    private UUID productId;
    private String productName;
    private BigDecimal lastAmount;
    private BigDecimal lastQuantity;
    private BigDecimal lastUnitPrice;
    private BigDecimal lastDiscount;
    private BigDecimal lastGstRate;
    private LocalDateTime billDate;
    private String invoiceNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
