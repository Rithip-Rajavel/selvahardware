package com.hardware.selvahardware.dto.product;

import com.hardware.selvahardware.enums.AuditAction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProductAuditDto {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productCode;
    private AuditAction action;
    private Integer oldStock;
    private Integer newStock;
    private Integer stockChange;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal priceChange;
    private BigDecimal cashAmount;
    private UUID performedBy;
    private String performedByName;
    private String notes;
    private LocalDateTime createdAt;
}
