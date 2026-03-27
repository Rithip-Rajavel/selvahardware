package com.hardware.selvahardware.dto.pricing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PriceMappingRequest {
    @NotNull
    private UUID customerId;

    @NotNull
    private UUID productId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal lastPrice;
}
