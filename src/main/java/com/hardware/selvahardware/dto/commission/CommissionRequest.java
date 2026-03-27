package com.hardware.selvahardware.dto.commission;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CommissionRequest {
    @NotNull
    private UUID contractorId;

    @NotBlank
    private String contractorName;

    @NotNull
    private UUID invoiceId;

    @NotBlank
    private String invoiceNumber;

    @NotBlank
    private String endCustomerName;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;
}
