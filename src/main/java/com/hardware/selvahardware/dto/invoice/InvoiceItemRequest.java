package com.hardware.selvahardware.dto.invoice;

import com.hardware.selvahardware.enums.PricingType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InvoiceItemRequest {
    @NotNull
    private UUID productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String productCode;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    private BigDecimal originalPrice;

    private BigDecimal lastPurchasedPrice;

    @NotNull
    private BigDecimal gstPercentage;

    private boolean withTax = true;

    @NotNull
    private BigDecimal gstAmount;

    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @NotNull
    private BigDecimal subtotal;

    @NotNull
    private BigDecimal total;

    private BigDecimal customTotal;

    private PricingType pricingType = PricingType.standard;

    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal area;
}
