package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.invoice.*;
import com.hardware.selvahardware.entity.*;
import com.hardware.selvahardware.enums.BillType;
import com.hardware.selvahardware.enums.InvoiceStatus;
import com.hardware.selvahardware.enums.TransactionType;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.InvoiceMapper;
import com.hardware.selvahardware.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final PriceMappingRepository priceMappingRepository;

    public List<InvoiceDto> getInvoices(UUID customerId, InvoiceStatus status, BillType billType,
                                         String fromDate, String toDate, String search) {
        
        // If no filters provided, return all invoices directly
        if (customerId == null && status == null && billType == null && 
            fromDate == null && toDate == null && (search == null || search.trim().isEmpty())) {
            return invoiceRepository.findAllInvoicesWithItems().stream()
                    .map(invoiceMapper::toDto)
                    .collect(Collectors.toList());
        }
        
        LocalDateTime from = fromDate != null ? LocalDateTime.parse(fromDate + "T00:00:00") : null;
        LocalDateTime to = toDate != null ? LocalDateTime.parse(toDate + "T23:59:59") : null;

        // Start with all invoices and apply filters dynamically
        List<Invoice> invoices = invoiceRepository.findAllInvoicesWithItems();
        
        // Apply filters in Java to avoid PostgreSQL parameter type issues
        if (customerId != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getCustomer().getId().equals(customerId))
                    .collect(Collectors.toList());
        }
        
        if (status != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        
        if (billType != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getBillType().equals(billType))
                    .collect(Collectors.toList());
        }
        
        if (from != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getCreatedAt().isAfter(from) || invoice.getCreatedAt().isEqual(from))
                    .collect(Collectors.toList());
        }
        
        if (to != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getCreatedAt().isBefore(to) || invoice.getCreatedAt().isEqual(to))
                    .collect(Collectors.toList());
        }
        
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getInvoiceNumber().toLowerCase().contains(searchLower) ||
                                    invoice.getCustomerName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }
        
        return invoices.stream().map(invoiceMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public InvoiceDto createInvoice(InvoiceRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        String invoiceNumber = generateInvoiceNumber(getSettings());

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .customer(customer)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerType(request.getCustomerType())
                .endCustomerName(request.getEndCustomerName())
                .commission(request.getCommission())
                .subtotal(request.getSubtotal())
                .totalDiscount(request.getTotalDiscount())
                .totalGst(request.getTotalGst())
                .grandTotal(request.getGrandTotal())
                .amountPaid(request.getAmountPaid())
                .balance(request.getBalance())
                .paymentMethod(request.getPaymentMethod())
                .status(request.getStatus())
                .billType(request.getBillType())
                .notes(request.getNotes())
                .build();

        List<InvoiceItem> items = request.getItems().stream().map(itemReq -> {
            InvoiceItem item = invoiceMapper.toItemEntity(itemReq, null);
            item.setInvoice(invoice);
            return item;
        }).collect(Collectors.toList());

        invoice.setItems(items);
        Invoice saved = invoiceRepository.save(invoice);

        // Update stock and price mapping for final bills
        if (BillType.final_bill.equals(request.getBillType())) {
            request.getItems().forEach(itemReq -> {
                productRepository.findById(itemReq.getProductId()).ifPresent(product -> {
                    int newStock = product.getStock() - itemReq.getQuantity().intValue();
                    product.setStock(Math.max(0, newStock));
                    productRepository.save(product);
                });

                // Update price mapping
                UUID customerId = request.getCustomerId();
                UUID productId = itemReq.getProductId();
                priceMappingRepository.findByCustomerIdAndProductId(customerId, productId)
                        .ifPresentOrElse(pm -> {
                            pm.setLastPrice(itemReq.getPrice());
                            priceMappingRepository.save(pm);
                        }, () -> {
                            PriceMapping pm = PriceMapping.builder()
                                    .customerId(customerId)
                                    .productId(productId)
                                    .lastPrice(itemReq.getPrice())
                                    .build();
                            priceMappingRepository.save(pm);
                        });
            });

            // Record billing transaction
            BigDecimal customerBalance = getCustomerBalance(customer.getId());
            Payment billingTx = Payment.builder()
                    .customer(customer)
                    .customerName(request.getCustomerName())
                    .invoiceId(invoice.getId())
                    .invoiceNumber(invoiceNumber)
                    .type(TransactionType.billing)
                    .amount(request.getGrandTotal())
                    .balanceAfter(customerBalance.add(request.getBalance()))
                    .paymentMethod(request.getPaymentMethod())
                    .notes(request.getNotes())
                    .build();
            paymentRepository.save(billingTx);
        }

        return invoiceMapper.toDto(saved);
    }

    public InvoiceDto getInvoiceById(UUID id) {
        return invoiceMapper.toDto(invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id)));
    }

    @Transactional
    public InvoiceDto payInvoice(UUID id, PayInvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));

        if (request.getAmount().compareTo(invoice.getBalance()) > 0) {
            throw new BusinessException("Payment amount exceeds invoice balance");
        }

        BigDecimal newAmountPaid = invoice.getAmountPaid().add(request.getAmount());
        BigDecimal newBalance = invoice.getBalance().subtract(request.getAmount());
        invoice.setAmountPaid(newAmountPaid);
        invoice.setBalance(newBalance);
        invoice.setStatus(newBalance.compareTo(BigDecimal.ZERO) <= 0 ? InvoiceStatus.paid : InvoiceStatus.pending);
        invoice.setPaymentMethod(request.getPaymentMethod());

        Invoice saved = invoiceRepository.save(invoice);

        // Record payment transaction
        BigDecimal currentTotalBalance = getCustomerBalance(invoice.getCustomer().getId());
        Payment paymentTx = Payment.builder()
                .customer(invoice.getCustomer())
                .customerName(invoice.getCustomerName())
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .type(TransactionType.payment)
                .amount(request.getAmount())
                .balanceAfter(currentTotalBalance.subtract(request.getAmount()))
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .build();
        paymentRepository.save(paymentTx);

        return invoiceMapper.toDto(saved);
    }

    public void deleteInvoice(UUID id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice", id);
        }
        invoiceRepository.deleteById(id);
    }

    public String getNextInvoiceNumber(String prefix) {
        return generateInvoiceNumberForPrefix(prefix != null ? prefix : "INV");
    }

    public InvoiceStatsDto getStats(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = now.toLocalDate().atTime(23, 59, 59);
        LocalDateTime monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        long total = invoiceRepository.count();
        long todayCount = invoiceRepository.countByDateRange(todayStart, todayEnd);
        BigDecimal todayRevenue = invoiceRepository.sumRevenueByDateRange(todayStart, todayEnd);
        BigDecimal monthlyRevenue = invoiceRepository.sumRevenueByDateRange(monthStart, now);
        BigDecimal pendingAmount = invoiceRepository.sumPendingBalance();

        return InvoiceStatsDto.builder()
                .totalInvoices(total)
                .todayInvoices(todayCount)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                .pendingAmount(pendingAmount != null ? pendingAmount : BigDecimal.ZERO)
                .build();
    }

    private String generateInvoiceNumber(String prefix) {
        return generateInvoiceNumberForPrefix(prefix);
    }

    private String generateInvoiceNumberForPrefix(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyMM"));
        String lastNumber = invoiceRepository.findLastInvoiceNumber(prefix + yearMonth).orElse(null);
        int counter = 1;
        if (lastNumber != null && lastNumber.length() > prefix.length() + 4) {
            try {
                counter = Integer.parseInt(lastNumber.substring(prefix.length() + 4)) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return prefix + yearMonth + String.format("%04d", counter);
    }

    private String getSettings() {
        return "INV"; // Default prefix; inject SettingsService if needed
    }

    private BigDecimal getCustomerBalance(UUID customerId) {
        List<Invoice> invoices = invoiceRepository.findByCustomerIdOrderByCreatedAtAsc(customerId);
        return invoices.stream().map(Invoice::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
