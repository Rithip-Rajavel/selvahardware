package com.hardware.selvahardware.dto.payment;

import com.hardware.selvahardware.enums.PaymentMethod;
import com.hardware.selvahardware.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDto {
    private String id;
    private String customerId;
    private String customerName;
    private String invoiceId;
    private String invoiceNumber;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private PaymentMethod paymentMethod;
    private String notes;
    private LocalDateTime createdAt;
}
