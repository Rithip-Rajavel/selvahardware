package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.payment.PaymentDto;
import com.hardware.selvahardware.dto.payment.PaymentRequest;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.entity.Payment;
import com.hardware.selvahardware.enums.TransactionType;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.PaymentMapper;
import com.hardware.selvahardware.repository.CustomerRepository;
import com.hardware.selvahardware.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final PaymentMapper paymentMapper;

    public List<PaymentDto> getPayments(UUID customerId, String type, String fromDate, String toDate) {
        TransactionType txType = type != null ? TransactionType.valueOf(type) : null;
        LocalDateTime from = fromDate != null ? LocalDateTime.parse(fromDate + "T00:00:00") : null;
        LocalDateTime to = toDate != null ? LocalDateTime.parse(toDate + "T23:59:59") : null;
        return paymentRepository.filterPayments(customerId, txType, from, to)
                .stream().map(paymentMapper::toDto).collect(Collectors.toList());
    }

    public PaymentDto createPayment(PaymentRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));
        Payment payment = Payment.builder()
                .customer(customer)
                .customerName(request.getCustomerName())
                .invoiceId(request.getInvoiceId())
                .invoiceNumber(request.getInvoiceNumber())
                .type(request.getType())
                .amount(request.getAmount())
                .balanceAfter(request.getBalanceAfter())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .build();
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public List<PaymentDto> getCustomerPayments(UUID customerId) {
        return paymentRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(paymentMapper::toDto).collect(Collectors.toList());
    }

    public void deletePayment(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment", id);
        }
        paymentRepository.deleteById(id);
    }
}
