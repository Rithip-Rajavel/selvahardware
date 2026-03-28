package com.hardware.selvahardware.dto.lastpaymentvalue;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LastPaymentValueRequest {
    
    @NotNull(message = "Customer ID is required")
    private UUID customerId;
    
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    @NotNull(message = "Last amount is required")
    @DecimalMin(value = "0.01", message = "Last amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Last amount must have maximum 8 integer digits and 2 decimal digits")
    private BigDecimal lastAmount;
    
    @NotNull(message = "Last quantity is required")
    @DecimalMin(value = "0.01", message = "Last quantity must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Last quantity must have maximum 8 integer digits and 2 decimal digits")
    private BigDecimal lastQuantity;
    
    @NotNull(message = "Last unit price is required")
    @DecimalMin(value = "0.01", message = "Last unit price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Last unit price must have maximum 8 integer digits and 2 decimal digits")
    private BigDecimal lastUnitPrice;
    
    @DecimalMin(value = "0.00", message = "Last discount must be 0 or greater")
    @Digits(integer = 8, fraction = 2, message = "Last discount must have maximum 8 integer digits and 2 decimal digits")
    private BigDecimal lastDiscount;
    
    @DecimalMin(value = "0.00", message = "Last GST rate must be 0 or greater")
    @Digits(integer = 3, fraction = 2, message = "Last GST rate must have maximum 3 integer digits and 2 decimal digits")
    private BigDecimal lastGstRate;
    
    private LocalDateTime billDate;
    
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceNumber;
}
