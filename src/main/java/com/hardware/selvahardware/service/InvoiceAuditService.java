package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.invoice.InvoiceAuditLogDto;
import com.hardware.selvahardware.dto.invoice.InvoiceAuditTrailDto;
import com.hardware.selvahardware.entity.*;
import com.hardware.selvahardware.enums.AuditActionType;
import com.hardware.selvahardware.enums.InvoiceStatus;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceAuditService {

        private final InvoiceAuditLogRepository auditLogRepository;
        private final InvoiceRepository invoiceRepository;
        private final PaymentRepository paymentRepository;
        private final InvoiceReturnRepository invoiceReturnRepository;
        private final CustomerRepository customerRepository;

        /**
         * Get complete audit trail for an invoice including all transactions,
         * payments, returns, and status changes
         */
        @Transactional(readOnly = true)
        public InvoiceAuditTrailDto getInvoiceAuditTrail(UUID invoiceId) {
                Invoice invoice = invoiceRepository.findById(invoiceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

                // Initialize lazy-loaded collections
                Hibernate.initialize(invoice.getItems());

                List<InvoiceAuditLog> auditLogs = auditLogRepository.findByInvoiceIdOrderByCreatedAtAsc(invoiceId);
                List<Payment> payments = paymentRepository.findAll().stream()
                                .filter(p -> p.getInvoiceId() != null && p.getInvoiceId().equals(invoiceId))
                                .collect(Collectors.toList());
                List<InvoiceReturn> returns = invoiceReturnRepository.findByInvoiceId(invoiceId);

                // Initialize lazy-loaded collections in returns
                for (InvoiceReturn returnItem : returns) {
                        Hibernate.initialize(returnItem.getItems());
                }

                return buildAuditTrailDto(invoice, auditLogs, payments, returns);
        }

        /**
         * Get audit logs for a specific customer
         */
        @Transactional(readOnly = true)
        public List<InvoiceAuditLogDto> getCustomerAuditLogs(UUID customerId) {
                return auditLogRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                                .stream()
                                .map(this::mapToDto)
                                .collect(Collectors.toList());
        }

        /**
         * Get all overdue payment audit logs
         */
        @Transactional(readOnly = true)
        public List<InvoiceAuditLogDto> getOverduePaymentLogs() {
                return auditLogRepository.findAllOverduePayments()
                                .stream()
                                .map(this::mapToDto)
                                .collect(Collectors.toList());
        }

        /**
         * Create audit log entry
         */
        @Transactional
        public InvoiceAuditLog createAuditLog(UUID invoiceId, AuditActionType actionType, String description,
                        BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter,
                        String notes, String createdBy) {
                Invoice invoice = invoiceRepository.findById(invoiceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

                InvoiceAuditLog log = InvoiceAuditLog.builder()
                                .invoiceId(invoiceId)
                                .invoiceNumber(invoice.getInvoiceNumber())
                                .customer(invoice.getCustomer())
                                .customerName(invoice.getCustomerName())
                                .actionType(actionType)
                                .actionDescription(description)
                                .amount(amount)
                                .balanceBefore(balanceBefore)
                                .balanceAfter(balanceAfter)
                                .amountPaidBefore(invoice.getAmountPaid()
                                                .subtract(amount != null ? amount : BigDecimal.ZERO))
                                .amountPaidAfter(invoice.getAmountPaid())
                                .notes(notes)
                                .createdBy(createdBy)
                                .createdAt(LocalDateTime.now())
                                .build();

                return auditLogRepository.save(log);
        }

        /**
         * Log payment received
         */
        @Transactional
        public void logPaymentReceived(UUID invoiceId, UUID paymentId, BigDecimal amount,
                        BigDecimal balanceBefore, BigDecimal balanceAfter,
                        String paymentMethod, String notes) {
                Invoice invoice = invoiceRepository.findById(invoiceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

                InvoiceAuditLog log = InvoiceAuditLog.builder()
                                .invoiceId(invoiceId)
                                .invoiceNumber(invoice.getInvoiceNumber())
                                .customer(invoice.getCustomer())
                                .customerName(invoice.getCustomerName())
                                .actionType(AuditActionType.PAYMENT_RECEIVED)
                                .actionDescription("Payment received: " + amount)
                                .amount(amount)
                                .balanceBefore(balanceBefore)
                                .balanceAfter(balanceAfter)
                                .amountPaidBefore(invoice.getAmountPaid().subtract(amount))
                                .amountPaidAfter(invoice.getAmountPaid())
                                .paymentMethod(paymentMethod)
                                .transactionId(paymentId)
                                .notes(notes)
                                .createdAt(LocalDateTime.now())
                                .build();

                auditLogRepository.save(log);
        }

        /**
         * Log return processed
         */
        @Transactional
        public void logReturnProcessed(UUID invoiceId, UUID returnId, BigDecimal returnAmount,
                        BigDecimal balanceBefore, BigDecimal balanceAfter, String reason) {
                Invoice invoice = invoiceRepository.findById(invoiceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

                InvoiceAuditLog log = InvoiceAuditLog.builder()
                                .invoiceId(invoiceId)
                                .invoiceNumber(invoice.getInvoiceNumber())
                                .customer(invoice.getCustomer())
                                .customerName(invoice.getCustomerName())
                                .actionType(AuditActionType.ITEM_RETURNED)
                                .actionDescription("Items returned: " + returnAmount)
                                .amount(returnAmount)
                                .balanceBefore(balanceBefore)
                                .balanceAfter(balanceAfter)
                                .returnId(returnId)
                                .notes(reason)
                                .createdAt(LocalDateTime.now())
                                .build();

                auditLogRepository.save(log);
        }

        /**
         * Log invoice creation
         */
        @Transactional
        public void logInvoiceCreated(UUID invoiceId) {
                Invoice invoice = invoiceRepository.findById(invoiceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

                InvoiceAuditLog log = InvoiceAuditLog.builder()
                                .invoiceId(invoiceId)
                                .invoiceNumber(invoice.getInvoiceNumber())
                                .customer(invoice.getCustomer())
                                .customerName(invoice.getCustomerName())
                                .actionType(AuditActionType.INVOICE_CREATED)
                                .actionDescription("Invoice created with total: " + invoice.getGrandTotal())
                                .amount(invoice.getGrandTotal())
                                .balanceBefore(BigDecimal.ZERO)
                                .balanceAfter(invoice.getBalance())
                                .amountPaidBefore(BigDecimal.ZERO)
                                .amountPaidAfter(invoice.getAmountPaid())
                                .createdAt(LocalDateTime.now())
                                .build();

                auditLogRepository.save(log);
        }

        /**
         * Log overdue payment
         */
        @Transactional
        public void logOverduePayment(UUID invoiceId, BigDecimal overdueAmount, int daysOverdue) {
                Invoice invoice = invoiceRepository.findById(invoiceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

                InvoiceAuditLog log = InvoiceAuditLog.builder()
                                .invoiceId(invoiceId)
                                .invoiceNumber(invoice.getInvoiceNumber())
                                .customer(invoice.getCustomer())
                                .customerName(invoice.getCustomerName())
                                .actionType(AuditActionType.OVERDUE_PAYMENT)
                                .actionDescription("Payment overdue by " + daysOverdue + " days")
                                .amount(overdueAmount)
                                .balanceBefore(invoice.getBalance())
                                .balanceAfter(invoice.getBalance())
                                .notes("Invoice is " + daysOverdue + " days overdue")
                                .createdAt(LocalDateTime.now())
                                .build();

                auditLogRepository.save(log);
        }

        private InvoiceAuditTrailDto buildAuditTrailDto(Invoice invoice, List<InvoiceAuditLog> auditLogs,
                        List<Payment> payments, List<InvoiceReturn> returns) {
                // Build timeline
                List<InvoiceAuditTrailDto.AuditEventDto> timeline = auditLogs.stream()
                                .map(log -> InvoiceAuditTrailDto.AuditEventDto.builder()
                                                .eventType(log.getActionType().toString())
                                                .description(log.getActionDescription())
                                                .amount(log.getAmount())
                                                .balanceAfter(log.getBalanceAfter())
                                                .notes(log.getNotes())
                                                .createdBy(log.getCreatedBy())
                                                .timestamp(log.getCreatedAt().toString())
                                                .build())
                                .collect(Collectors.toList());

                // Build payment history
                List<InvoiceAuditTrailDto.PaymentSummaryDto> paymentHistory = payments.stream()
                                .map(p -> InvoiceAuditTrailDto.PaymentSummaryDto.builder()
                                                .paymentId(p.getId())
                                                .amount(p.getAmount())
                                                .paymentMethod(p.getPaymentMethod() != null
                                                                ? p.getPaymentMethod().toString()
                                                                : null)
                                                .notes(p.getNotes())
                                                .timestamp(p.getCreatedAt().toString())
                                                .build())
                                .collect(Collectors.toList());

                // Build return history
                List<InvoiceAuditTrailDto.ReturnSummaryDto> returnHistory = returns.stream()
                                .map(r -> InvoiceAuditTrailDto.ReturnSummaryDto.builder()
                                                .returnId(r.getId())
                                                .returnNumber(r.getReturnNumber())
                                                .totalReturnAmount(r.getTotalReturnAmount())
                                                .itemCount(r.getItems() != null ? r.getItems().size() : 0)
                                                .reason(r.getReason())
                                                .status(r.getStatus().toString())
                                                .timestamp(r.getCreatedAt().toString())
                                                .build())
                                .collect(Collectors.toList());

                // Calculate totals
                BigDecimal totalReturned = returns.stream()
                                .filter(r -> r.getStatus().toString().equals("processed")
                                                || r.getStatus().toString().equals("refunded"))
                                .map(InvoiceReturn::getTotalReturnAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                long overdueCount = auditLogs.stream()
                                .filter(l -> l.getActionType() == AuditActionType.OVERDUE_PAYMENT)
                                .count();

                return InvoiceAuditTrailDto.builder()
                                .invoiceId(invoice.getId())
                                .invoiceNumber(invoice.getInvoiceNumber())
                                .customerId(invoice.getCustomer().getId())
                                .customerName(invoice.getCustomerName())
                                .customerPhone(invoice.getCustomerPhone())
                                .originalGrandTotal(invoice.getGrandTotal())
                                .originalBalance(invoice.getBalance().add(totalReturned))
                                .currentStatus(invoice.getStatus().toString())
                                .currentBalance(invoice.getBalance())
                                .totalPaid(invoice.getAmountPaid())
                                .totalReturned(totalReturned)
                                .totalTransactions(auditLogs.size())
                                .totalPayments(payments.size())
                                .totalReturns(returns.size())
                                .totalOverdueEvents((int) overdueCount)
                                .timeline(timeline)
                                .paymentHistory(paymentHistory)
                                .returnHistory(returnHistory)
                                .build();
        }

        private InvoiceAuditLogDto mapToDto(InvoiceAuditLog log) {
                return InvoiceAuditLogDto.builder()
                                .id(log.getId())
                                .invoiceId(log.getInvoiceId())
                                .invoiceNumber(log.getInvoiceNumber())
                                .customerId(log.getCustomer() != null ? log.getCustomer().getId() : null)
                                .customerName(log.getCustomerName())
                                .actionType(log.getActionType())
                                .actionDescription(log.getActionDescription())
                                .amount(log.getAmount())
                                .balanceBefore(log.getBalanceBefore())
                                .balanceAfter(log.getBalanceAfter())
                                .amountPaidBefore(log.getAmountPaidBefore())
                                .amountPaidAfter(log.getAmountPaidAfter())
                                .paymentMethod(log.getPaymentMethod())
                                .transactionId(log.getTransactionId())
                                .returnId(log.getReturnId())
                                .notes(log.getNotes())
                                .createdBy(log.getCreatedBy())
                                .createdAt(log.getCreatedAt())
                                .build();
        }
}
