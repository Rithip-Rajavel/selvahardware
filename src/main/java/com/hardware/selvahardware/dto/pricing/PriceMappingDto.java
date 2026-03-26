package com.hardware.selvahardware.dto.pricing;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PriceMappingDto {
    private String id;
    private String customerId;
    private String productId;
    private BigDecimal lastPrice;
    private LocalDateTime updatedAt;
}
