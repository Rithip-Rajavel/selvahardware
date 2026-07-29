package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.invoice.InvoiceReturnDto;
import com.hardware.selvahardware.dto.invoice.InvoiceReturnRequest;
import com.hardware.selvahardware.entity.*;
import com.hardware.selvahardware.enums.ReturnStatus;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
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
public class InvoiceReturnService {

    private final InvoiceReturnRepository invoiceReturnRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceAuditService auditService;

    /**
     * Get all returns
     */
    @Transactional(readOnly = true)
    public List<InvoiceReturnDto> getAllReturns() {
        return invoiceReturnRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all returns for an invoice
     */
    @Transactional(readOnly = true)
    public List<InvoiceReturnDto> getReturnsByInvoice(UUID invoiceId) {
        return invoiceReturnRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get return by ID
     */
    @Transactional(readOnly = true)
    public InvoiceReturnDto getReturnById(UUID returnId) {
        InvoiceReturn invoiceReturn = invoiceReturnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice Return", returnId));
        return mapToDto(invoiceReturn);
    }

    /**
     * Get all returns for a customer
     */
    @Transactional(readOnly = true)
    public List<InvoiceReturnDto> getReturnsByCustomer(UUID customerId) {
        return invoiceReturnRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new return request
     */
    @Transactional
    public InvoiceReturnDto createReturn(InvoiceReturnRequest request, String processedBy) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", request.getInvoiceId()));

        Customer customer = invoice.getCustomer();

        // Generate return number
        String returnNumber = generateReturnNumber();

        // Create return entity
        InvoiceReturn invoiceReturn = InvoiceReturn.builder()
                .returnNumber(returnNumber)
                .invoice(invoice)
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customer(customer)
                .customerName(invoice.getCustomerName())
                .status(ReturnStatus.pending)
                .reason(request.getReason())
                .processedBy(processedBy)
                .createdAt(LocalDateTime.now())
                .build();

        // Process items
        BigDecimal totalReturnAmount = BigDecimal.ZERO;
        BigDecimal totalReturnQuantity = BigDecimal.ZERO;

        List<InvoiceReturnItem> returnItems = request.getItems().stream()
                .map(itemReq -> {
                    InvoiceItem invoiceItem = invoiceItemRepository.findById(itemReq.getInvoiceItemId())
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Invoice Item", itemReq.getInvoiceItemId()));

                    // Validate quantity
                    if (itemReq.getQuantity().compareTo(invoiceItem.getQuantity()) > 0) {
                        throw new BusinessException(
                                "Return quantity exceeds original quantity for item: " + invoiceItem.getProductName());
                    }

                    BigDecimal itemTotal = invoiceItem.getPrice().multiply(itemReq.getQuantity());
                    BigDecimal gstAmount = itemTotal.multiply(invoiceItem.getGstPercentage())
                            .divide(new BigDecimal("100"));
                    BigDecimal discount = invoiceItem.getDiscount().multiply(itemReq.getQuantity())
                            .divide(invoiceItem.getQuantity());
                    BigDecimal subtotal = itemTotal.subtract(discount);
                    BigDecimal total = subtotal.add(gstAmount);

                    InvoiceReturnItem returnItem = InvoiceReturnItem.builder()
                            .invoiceReturn(invoiceReturn)
                            .invoiceItemId(invoiceItem.getId())
                            .productId(invoiceItem.getProductId())
                            .productName(invoiceItem.getProductName())
                            .productCode(invoiceItem.getProductCode())
                            .quantity(itemReq.getQuantity())
                            .price(invoiceItem.getPrice())
                            .gstPercentage(invoiceItem.getGstPercentage())
                            .gstAmount(gstAmount)
                            .discount(discount)
                            .subtotal(subtotal)
                            .total(total)
                            .reason(itemReq.getReason())
                            .originalInvoiceQuantity(invoiceItem.getQuantity())
                            .build();

                    return returnItem;
                })
                .collect(Collectors.toList());

        // Calculate totals
        for (InvoiceReturnItem item : returnItems) {
            totalReturnAmount = totalReturnAmount.add(item.getTotal());
            totalReturnQuantity = totalReturnQuantity.add(item.getQuantity());
        }

        invoiceReturn.setItems(returnItems);
        invoiceReturn.setTotalReturnAmount(totalReturnAmount);
        invoiceReturn.setTotalReturnQuantity(totalReturnQuantity);

        InvoiceReturn saved = invoiceReturnRepository.save(invoiceReturn);
        return mapToDto(saved);
    }

    /**
     * Process/approve a return - updates stock and invoice balance
     */
    @Transactional
    public InvoiceReturnDto processReturn(UUID returnId, String refundMethod, String processedBy) {
        InvoiceReturn invoiceReturn = invoiceReturnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice Return", returnId));

        if (invoiceReturn.getStatus() != ReturnStatus.pending) {
            throw new BusinessException("Return is not in pending status");
        }

        Invoice invoice = invoiceReturn.getInvoice();
        BigDecimal balanceBefore = invoice.getBalance();

        // Update stock for each returned item
        for (InvoiceReturnItem item : invoiceReturn.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", item.getProductId()));

            // Increase stock
            int newStock = product.getStock() + item.getQuantity().intValue();
            product.setStock(newStock);
            productRepository.save(product);
        }

        // Update invoice balance
        BigDecimal newBalance = invoice.getBalance().subtract(invoiceReturn.getTotalReturnAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            newBalance = BigDecimal.ZERO;
        }
        invoice.setBalance(newBalance);

        // Update invoice status if balance is 0
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(com.hardware.selvahardware.enums.InvoiceStatus.paid);
        }

        invoiceRepository.save(invoice);

        // Update return status
        invoiceReturn.setStatus(ReturnStatus.processed);
        invoiceReturn.setRefundMethod(refundMethod);
        invoiceReturn.setProcessedBy(processedBy);
        invoiceReturn.setProcessedAt(LocalDateTime.now());
        invoiceReturn.setRefundAmount(invoiceReturn.getTotalReturnAmount());

        InvoiceReturn saved = invoiceReturnRepository.save(invoiceReturn);

        // Log in audit
        auditService.logReturnProcessed(invoice.getId(), returnId, invoiceReturn.getTotalReturnAmount(),
                balanceBefore, newBalance, invoiceReturn.getReason());

        return mapToDto(saved);
    }

    /**
     * Refund processed return
     */
    @Transactional
    public InvoiceReturnDto refundReturn(UUID returnId, String refundMethod, String processedBy) {
        InvoiceReturn invoiceReturn = invoiceReturnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice Return", returnId));

        if (invoiceReturn.getStatus() != ReturnStatus.processed) {
            throw new BusinessException("Return must be processed before refund");
        }

        invoiceReturn.setStatus(ReturnStatus.refunded);
        invoiceReturn.setRefundMethod(refundMethod);
        invoiceReturn.setProcessedBy(processedBy);
        invoiceReturn.setProcessedAt(LocalDateTime.now());
        invoiceReturn.setRefundAmount(invoiceReturn.getTotalReturnAmount());

        InvoiceReturn saved = invoiceReturnRepository.save(invoiceReturn);
        return mapToDto(saved);
    }

    /**
     * Cancel a return
     */
    @Transactional
    public void cancelReturn(UUID returnId) {
        InvoiceReturn invoiceReturn = invoiceReturnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice Return", returnId));

        if (invoiceReturn.getStatus() == ReturnStatus.processed || invoiceReturn.getStatus() == ReturnStatus.refunded) {
            throw new BusinessException("Cannot cancel already processed return");
        }

        invoiceReturn.setStatus(ReturnStatus.cancelled);
        invoiceReturnRepository.save(invoiceReturn);
    }

    /**
     * Get total returned amount for a customer
     */
    public BigDecimal getTotalReturnedAmountByCustomer(UUID customerId) {
        return invoiceReturnRepository.sumTotalReturnedAmountByCustomer(customerId);
    }

    private String generateReturnNumber() {
        LocalDateTime now = LocalDateTime.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyMM"));
        String timestamp = now.format(DateTimeFormatter.ofPattern("ddHHmmss"));
        return "RET" + yearMonth + timestamp;
    }

    private InvoiceReturnDto mapToDto(InvoiceReturn invoiceReturn) {
        // Initialize lazy-loaded collections
        Hibernate.initialize(invoiceReturn.getItems());

        List<InvoiceReturnDto.InvoiceReturnItemDto> itemDtos = invoiceReturn.getItems() != null
                ? invoiceReturn.getItems().stream()
                        .map(item -> InvoiceReturnDto.InvoiceReturnItemDto.builder()
                                .id(item.getId())
                                .invoiceItemId(item.getInvoiceItemId())
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .productCode(item.getProductCode())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .gstPercentage(item.getGstPercentage())
                                .gstAmount(item.getGstAmount())
                                .discount(item.getDiscount())
                                .subtotal(item.getSubtotal())
                                .total(item.getTotal())
                                .reason(item.getReason())
                                .originalInvoiceQuantity(item.getOriginalInvoiceQuantity())
                                .build())
                        .collect(Collectors.toList())
                : null;

        return InvoiceReturnDto.builder()
                .id(invoiceReturn.getId())
                .returnNumber(invoiceReturn.getReturnNumber())
                .invoiceId(invoiceReturn.getInvoiceId())
                .invoiceNumber(invoiceReturn.getInvoiceNumber())
                .customerId(invoiceReturn.getCustomer().getId())
                .customerName(invoiceReturn.getCustomerName())
                .totalReturnAmount(invoiceReturn.getTotalReturnAmount())
                .totalReturnQuantity(invoiceReturn.getTotalReturnQuantity())
                .status(invoiceReturn.getStatus())
                .refundAmount(invoiceReturn.getRefundAmount())
                .refundMethod(invoiceReturn.getRefundMethod())
                .reason(invoiceReturn.getReason())
                .processedBy(invoiceReturn.getProcessedBy())
                .processedAt(invoiceReturn.getProcessedAt())
                .items(itemDtos)
                .createdAt(invoiceReturn.getCreatedAt())
                .updatedAt(invoiceReturn.getUpdatedAt())
                .build();
    }
}
