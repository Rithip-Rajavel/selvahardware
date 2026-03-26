package com.hardware.selvahardware.dto.customer;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CustomerBalanceDto {
    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal pendingBalance;
    private long invoiceCount;
}
