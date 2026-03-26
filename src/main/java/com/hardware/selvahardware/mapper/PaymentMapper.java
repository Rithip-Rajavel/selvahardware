package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.payment.PaymentDto;
import com.hardware.selvahardware.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .customerId(payment.getCustomer().getId())
                .customerName(payment.getCustomerName())
                .invoiceId(payment.getInvoiceId())
                .invoiceNumber(payment.getInvoiceNumber())
                .type(payment.getType())
                .amount(payment.getAmount())
                .balanceAfter(payment.getBalanceAfter())
                .paymentMethod(payment.getPaymentMethod())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
