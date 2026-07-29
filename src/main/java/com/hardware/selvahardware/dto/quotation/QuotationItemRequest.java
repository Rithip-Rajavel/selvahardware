package com.hardware.selvahardware.dto.quotation;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationItemRequest {
    
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    @NotBlank(message = "Product name is required")
    private String productName;
    
    private String productCode;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotBlank(message = "Unit is required")
    private String unit;
    
    @NotNull(message = "GST percentage is required")
    @DecimalMin(value = "0.00", message = "GST percentage cannot be negative")
    private BigDecimal gstPercentage;
    
    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    private BigDecimal discount;
    
    private String description;
}
