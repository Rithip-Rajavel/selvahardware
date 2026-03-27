package com.hardware.selvahardware.dto.pending;

import com.hardware.selvahardware.dto.invoice.InvoiceItemRequest;
import com.hardware.selvahardware.enums.CustomerType;
import com.hardware.selvahardware.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class PendingBillRequest {
    @NotBlank
    private UUID customerId;

    @NotBlank
    private String customerName;

    @NotNull
    private CustomerType customerType;

    @NotNull
    private PaymentMethod paymentMethod;

    private BigDecimal billDiscount = BigDecimal.ZERO;
    private BigDecimal amountPaid = BigDecimal.ZERO;
    private String endCustomerName;
    private BigDecimal commission = BigDecimal.ZERO;
    private String notes;

    @Valid
    private List<InvoiceItemRequest> items;
}
