package com.hardware.selvahardware.dto.commission;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommissionDto {
    private UUID id;
    private UUID contractorId;
    private String contractorName;
    private UUID invoiceId;
    private String invoiceNumber;
    private String endCustomerName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
