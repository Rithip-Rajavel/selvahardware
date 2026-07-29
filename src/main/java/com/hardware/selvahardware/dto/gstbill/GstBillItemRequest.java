package com.hardware.selvahardware.dto.gstbill;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstBillItemRequest {

    @NotNull(message = "Item number is required")
    private Integer itemNumber;

    @NotBlank(message = "Particulars is required")
    private String particulars;

    @NotBlank(message = "HSN code is required")
    @Size(min = 4, max = 20, message = "HSN code must be 4-20 digits")
    @Pattern(regexp = "^\\d{4,20}$", message = "HSN code must contain only digits")
    private String hsnCode;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private BigDecimal quantity;

    @NotNull(message = "Rate is required")
    @Positive(message = "Rate must be a positive number")
    private BigDecimal rate;

    @NotNull(message = "Total is required")
    @Positive(message = "Total must be a positive number")
    private BigDecimal total;
}
