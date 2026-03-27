package com.hardware.selvahardware.dto.product;

import com.hardware.selvahardware.enums.PricingType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductDto {
    private UUID id;
    private String name;
    private String code;
    private String barcode;
    private BigDecimal price;
    private int stock;
    private String unit;
    private BigDecimal gstPercentage;
    private String category;
    private String description;
    private PricingType pricingType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
