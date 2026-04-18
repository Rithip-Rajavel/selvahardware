package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.invoice.OverdueBillDto;
import com.hardware.selvahardware.dto.invoice.OverdueSummaryDto;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.entity.Payment;
import com.hardware.selvahardware.enums.InvoiceStatus;
import com.hardware.selvahardware.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OverdueBillsService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceReturnRepository invoiceReturnRepository;
    private final InvoiceAuditService auditService;

    // Default due date is 30 days after invoice creation
    private static final int DEFAULT_CREDIT_DAYS = 30;

    /**
     * Get all overdue bills
     */
    @Transactional(readOnly = true)
    public List<OverdueBillDto> getAllOverdueBills() {
        List<Invoice> pendingInvoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.pending)
                .collect(Collectors.toList());

        List<OverdueBillDto> overdueBills = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Invoice invoice : pendingInvoices) {
            int daysOverdue = calculateDaysOverdue(invoice, now);
            if (daysOverdue > 0) {
                overdueBills.add(buildOverdueBillDto(invoice, daysOverdue));
            }
        }

        // Sort by days overdue descending
        overdueBills.sort((a, b) -> b.getDaysOverdue().compareTo(a.getDaysOverdue()));

        return overdueBills;
    }

    /**
     * Get overdue bills for a specific customer
     */
    @Transactional(readOnly = true)
    public List<OverdueBillDto> getOverdueBillsByCustomer(UUID customerId) {
        List<Invoice> pendingInvoices = invoiceRepository.findByCustomerIdOrderByCreatedAtAsc(customerId)
                .stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.pending)
                .collect(Collectors.toList());

        List<OverdueBillDto> overdueBills = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Invoice invoice : pendingInvoices) {
            int daysOverdue = calculateDaysOverdue(invoice, now);
            if (daysOverdue > 0) {
                overdueBills.add(buildOverdueBillDto(invoice, daysOverdue));
            }
        }

        overdueBills.sort((a, b) -> b.getDaysOverdue().compareTo(a.getDaysOverdue()));
        return overdueBills;
    }

    /**
     * Get overdue summary for dashboard
     */
    @Transactional(readOnly = true)
    public OverdueSummaryDto getOverdueSummary() {
        List<Invoice> pendingInvoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.pending)
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
        List<OverdueBillDto> overdueBills = new ArrayList<>();

        long overdue1To30 = 0, overdue31To60 = 0, overdue61To90 = 0, overdue90Plus = 0;
        BigDecimal amount1To30 = BigDecimal.ZERO, amount31To60 = BigDecimal.ZERO,
                amount61To90 = BigDecimal.ZERO, amount90Plus = BigDecimal.ZERO;

        Set<UUID> customersWithOverdue = new HashSet<>();
        Set<UUID> contractorsWithOverdue = new HashSet<>();

        for (Invoice invoice : pendingInvoices) {
            int daysOverdue = calculateDaysOverdue(invoice, now);
            if (daysOverdue > 0) {
                OverdueBillDto dto = buildOverdueBillDto(invoice, daysOverdue);
                overdueBills.add(dto);
                customersWithOverdue.add(invoice.getCustomer().getId());

                if (invoice.getContractor() != null) {
                    contractorsWithOverdue.add(invoice.getContractor().getId());
                }

                // Aging buckets
                if (daysOverdue <= 30) {
                    overdue1To30++;
                    amount1To30 = amount1To30.add(invoice.getBalance());
                } else if (daysOverdue <= 60) {
                    overdue31To60++;
                    amount31To60 = amount31To60.add(invoice.getBalance());
                } else if (daysOverdue <= 90) {
                    overdue61To90++;
                    amount61To90 = amount61To90.add(invoice.getBalance());
                } else {
                    overdue90Plus++;
                    amount90Plus = amount90Plus.add(invoice.getBalance());
                }
            }
        }

        // Sort and get top overdue
        overdueBills.sort((a, b) -> b.getDaysOverdue().compareTo(a.getDaysOverdue()));
        List<OverdueBillDto> topOverdue = overdueBills.stream().limit(10).collect(Collectors.toList());

        // Build customer summary
        Map<UUID, List<OverdueBillDto>> customerOverdueMap = overdueBills.stream()
                .collect(Collectors.groupingBy(OverdueBillDto::getCustomerId));

        List<OverdueSummaryDto.CustomerOverdueSummaryDto> customerSummaries = customerOverdueMap.entrySet().stream()
                .map(entry -> {
                    UUID customerId = entry.getKey();
                    List<OverdueBillDto> bills = entry.getValue();
                    BigDecimal totalOverdue = bills.stream()
                            .map(OverdueBillDto::getOverdueAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    int maxDays = bills.stream()
                            .mapToInt(OverdueBillDto::getDaysOverdue)
                            .max().orElse(0);

                    return OverdueSummaryDto.CustomerOverdueSummaryDto.builder()
                            .customerId(customerId.toString())
                            .customerName(bills.get(0).getCustomerName())
                            .customerPhone(bills.get(0).getCustomerPhone())
                            .overdueInvoiceCount(bills.size())
                            .totalOverdueAmount(totalOverdue)
                            .maxDaysOverdue(maxDays)
                            .build();
                })
                .sorted((a, b) -> b.getTotalOverdueAmount().compareTo(a.getTotalOverdueAmount()))
                .limit(5)
                .collect(Collectors.toList());

        BigDecimal totalOverdueAmount = overdueBills.stream()
                .map(OverdueBillDto::getOverdueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OverdueSummaryDto.builder()
                .totalOverdueInvoices(overdueBills.size())
                .totalOverdueAmount(totalOverdueAmount)
                .totalCustomersWithOverdue(customersWithOverdue.size())
                .totalContractorsWithOverdue(contractorsWithOverdue.size())
                .invoicesOverdue1To30Days(overdue1To30)
                .invoicesOverdue31To60Days(overdue31To60)
                .invoicesOverdue61To90Days(overdue61To90)
                .invoicesOverdueMoreThan90Days(overdue90Plus)
                .amountOverdue1To30Days(amount1To30)
                .amountOverdue31To60Days(amount31To60)
                .amountOverdue61To90Days(amount61To90)
                .amountOverdueMoreThan90Days(amount90Plus)
                .topOverdueInvoices(topOverdue)
                .customersWithMostOverdue(customerSummaries)
                .build();
    }

    /**
     * Mark an invoice as overdue and log it
     */
    public void checkAndMarkOverdue(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null || invoice.getStatus() != InvoiceStatus.pending) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        int daysOverdue = calculateDaysOverdue(invoice, now);

        if (daysOverdue > 0) {
            // Log overdue event
            auditService.logOverduePayment(invoiceId, invoice.getBalance(), daysOverdue);
        }
    }

    /**
     * Check all invoices and mark overdue ones
     */
    public void checkAllOverdueInvoices() {
        List<Invoice> pendingInvoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.pending)
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();

        for (Invoice invoice : pendingInvoices) {
            int daysOverdue = calculateDaysOverdue(invoice, now);
            if (daysOverdue > 0 && shouldLogOverdue(invoice)) {
                auditService.logOverduePayment(invoice.getId(), invoice.getBalance(), daysOverdue);
            }
        }
    }

    private int calculateDaysOverdue(Invoice invoice, LocalDateTime now) {
        LocalDateTime dueDate = invoice.getCreatedAt().plusDays(DEFAULT_CREDIT_DAYS);
        if (now.isAfter(dueDate)) {
            return (int) ChronoUnit.DAYS.between(dueDate, now);
        }
        return 0;
    }

    private boolean shouldLogOverdue(Invoice invoice) {
        // Check if we already logged overdue for this invoice today
        // This prevents duplicate overdue logs
        return true; // Simplified - in production, check last overdue log date
    }

    private OverdueBillDto buildOverdueBillDto(Invoice invoice, int daysOverdue) {
        Customer customer = invoice.getCustomer();

        // Get last payment for this invoice
        Payment lastPayment = paymentRepository.findAll().stream()
                .filter(p -> p.getInvoiceId() != null && p.getInvoiceId().equals(invoice.getId()))
                .max(Comparator.comparing(Payment::getCreatedAt))
                .orElse(null);

        // Count total overdue invoices for customer
        long totalOverdueForCustomer = invoiceRepository.findByCustomerIdOrderByCreatedAtAsc(customer.getId())
                .stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.pending)
                .filter(inv -> calculateDaysOverdue(inv, LocalDateTime.now()) > 0)
                .count();

        BigDecimal totalOverdueAmountForCustomer = invoiceRepository
                .findByCustomerIdOrderByCreatedAtAsc(customer.getId())
                .stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.pending)
                .filter(inv -> calculateDaysOverdue(inv, LocalDateTime.now()) > 0)
                .map(Invoice::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OverdueBillDto.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerId(customer.getId())
                .customerName(invoice.getCustomerName())
                .customerPhone(invoice.getCustomerPhone())
                .customerType(invoice.getCustomerType() != null ? invoice.getCustomerType().toString() : null)
                .grandTotal(invoice.getGrandTotal())
                .amountPaid(invoice.getAmountPaid())
                .balance(invoice.getBalance())
                .totalDue(invoice.getBalance())
                .invoiceDate(invoice.getCreatedAt())
                .dueDate(invoice.getCreatedAt().plusDays(DEFAULT_CREDIT_DAYS))
                .daysOverdue(daysOverdue)
                .invoiceStatus(invoice.getStatus().toString())
                .isOverdue(true)
                .overdueAmount(invoice.getBalance())
                .lastPaymentDate(lastPayment != null ? lastPayment.getCreatedAt() : null)
                .lastPaymentAmount(lastPayment != null ? lastPayment.getAmount() : null)
                .contractorId(invoice.getContractor() != null ? invoice.getContractor().getId() : null)
                .contractorName(invoice.getContractorName())
                .totalOverdueInvoicesForCustomer((int) totalOverdueForCustomer)
                .totalOverdueAmountForCustomer(totalOverdueAmountForCustomer)
                .build();
    }
}
