package com.hardware.selvahardware.dto.product;

import com.hardware.selvahardware.enums.PricingType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String barcode;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @Min(0)
    private int stock;

    @NotBlank
    private String unit;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal gstPercentage;

    private String category;
    private String description;
    private PricingType pricingType = PricingType.standard;
}
