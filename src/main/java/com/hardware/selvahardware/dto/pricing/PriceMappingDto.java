package com.hardware.selvahardware.dto.pricing;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PriceMappingDto {
    private UUID id;
    private UUID customerId;
    private UUID productId;
    private BigDecimal lastPrice;
    private LocalDateTime updatedAt;
}
