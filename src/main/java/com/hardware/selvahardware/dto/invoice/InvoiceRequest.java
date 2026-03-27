package com.hardware.selvahardware.dto.invoice;

import com.hardware.selvahardware.enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceRequest {
    @NotBlank
    private UUID customerId;

    @NotBlank
    private String customerName;

    private String customerPhone;

    @NotNull
    private CustomerType customerType;

    private String endCustomerName;
    private BigDecimal commission;

    @NotEmpty
    @Valid
    private List<InvoiceItemRequest> items;

    @NotNull
    private BigDecimal subtotal;

    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @NotNull
    private BigDecimal totalGst;

    @NotNull
    private BigDecimal grandTotal;

    @NotNull
    private BigDecimal amountPaid;

    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull
    private PaymentMethod paymentMethod;

    private InvoiceStatus status = InvoiceStatus.pending;
    private BillType billType = BillType.final_bill;
    private String notes;
}
