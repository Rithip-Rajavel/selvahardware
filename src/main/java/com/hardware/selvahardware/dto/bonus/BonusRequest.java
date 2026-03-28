package com.hardware.selvahardware.dto.bonus;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusRequest {
    
    @NotNull(message = "Customer ID is required")
    private UUID customerId;
    
    @NotNull(message = "Bonus amount is required")
    @DecimalMin(value = "0.01", message = "Bonus amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Bonus amount must have maximum 8 integer digits and 2 decimal digits")
    private BigDecimal bonusAmount;
    
    @NotNull(message = "Bonus month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer bonusMonth;
    
    @NotNull(message = "Bonus year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer bonusYear;
    
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
