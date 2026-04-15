package com.hardware.selvahardware.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateRequest {
    @NotNull
    @Min(0)
    private Integer quantity;

    @NotBlank
    private String operation; // "add" or "subtract"

    private java.math.BigDecimal cashAmount; // Cash amount when stock is decreased

    private String notes;
}
