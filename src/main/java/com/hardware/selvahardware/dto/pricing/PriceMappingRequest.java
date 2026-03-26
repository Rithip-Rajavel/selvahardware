package com.hardware.selvahardware.dto.pricing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceMappingRequest {
    @NotBlank
    private String customerId;

    @NotBlank
    private String productId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal lastPrice;
}
