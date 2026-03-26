package com.hardware.selvahardware.dto.commission;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommissionRequest {
    @NotBlank
    private String contractorId;

    @NotBlank
    private String contractorName;

    @NotBlank
    private String invoiceId;

    @NotBlank
    private String invoiceNumber;

    @NotBlank
    private String endCustomerName;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;
}
