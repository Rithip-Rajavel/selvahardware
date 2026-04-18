package com.hardware.selvahardware.dto.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating invoice return
 */
@Data
public class InvoiceReturnRequest {

    @NotNull
    private UUID invoiceId;

    private String reason;

    @NotEmpty
    @Valid
    private List<ReturnItemRequest> items;

    @Data
    public static class ReturnItemRequest {
        @NotNull
        private UUID invoiceItemId;

        @NotNull
        private BigDecimal quantity;

        private String reason;
    }
}
