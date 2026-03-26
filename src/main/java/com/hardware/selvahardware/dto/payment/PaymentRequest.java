package com.hardware.selvahardware.dto.payment;

import com.hardware.selvahardware.enums.PaymentMethod;
import com.hardware.selvahardware.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotBlank
    private String customerId;

    @NotBlank
    private String customerName;

    private String invoiceId;
    private String invoiceNumber;

    @NotNull
    private TransactionType type;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private BigDecimal balanceAfter;

    @NotNull
    private PaymentMethod paymentMethod;

    private String notes;
}
