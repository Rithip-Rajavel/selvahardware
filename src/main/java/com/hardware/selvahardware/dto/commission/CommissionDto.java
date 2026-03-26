package com.hardware.selvahardware.dto.commission;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CommissionDto {
    private String id;
    private String contractorId;
    private String contractorName;
    private String invoiceId;
    private String invoiceNumber;
    private String endCustomerName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
