package com.hardware.selvahardware.dto.gstbill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstBillRequest {

    @NotNull(message = "Invoice ID is required")
    private UUID invoiceId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    private String customerName;

    @Size(max = 1000, message = "Customer address must not exceed 1000 characters")
    private String customerAddress;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 100, message = "Invoice number must not exceed 100 characters")
    private String invoiceNumber;

    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    @NotNull(message = "Subtotal is required")
    @Positive(message = "Subtotal must be a positive number")
    private BigDecimal subtotal;

    @NotNull(message = "SGST amount is required")
    @Positive(message = "SGST amount must be a positive number")
    private BigDecimal sgstAmount;

    @NotNull(message = "CGST amount is required")
    @Positive(message = "CGST amount must be a positive number")
    private BigDecimal cgstAmount;

    @Positive(message = "IGST amount must be a positive number")
    private BigDecimal igstAmount;

    @NotNull(message = "Grand total is required")
    @Positive(message = "Grand total must be a positive number")
    private BigDecimal grandTotal;

    @Size(max = 500, message = "Amount in words must not exceed 500 characters")
    private String amountInWords;

    @Valid
    @NotEmpty(message = "At least one item is required")
    private List<GstBillItemRequest> items;
}
