package com.hardware.selvahardware.dto.quotation;

import com.hardware.selvahardware.enums.CustomerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String customerPhone;

    @NotNull(message = "Customer type is required")
    private CustomerType customerType;

    private String endCustomerName;
    private UUID contractorId;
    private String contractorName;
    private BigDecimal commission;

    @NotNull(message = "Items list is required")
    @Size(min = 1, message = "At least one item is required")
    @Valid
    private List<QuotationItemRequest> items;

    @Future(message = "Valid until date must be in the future")
    private LocalDateTime validUntil;

    private String notes;

    private String termsConditions;
}
