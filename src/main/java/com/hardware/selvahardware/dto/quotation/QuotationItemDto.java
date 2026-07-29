package com.hardware.selvahardware.dto.quotation;

import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationItemDto {
    private UUID id;
    private UUID quotationId;
    private UUID productId;
    private String productName;
    private String productCode;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String unit;
    private BigDecimal gstPercentage;
    private BigDecimal discount;
    private BigDecimal total;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
