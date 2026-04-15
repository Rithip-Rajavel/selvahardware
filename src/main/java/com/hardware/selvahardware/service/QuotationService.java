package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.quotation.QuotationDto;
import com.hardware.selvahardware.dto.quotation.QuotationRequest;
import com.hardware.selvahardware.entity.Quotation;
import com.hardware.selvahardware.entity.QuotationItem;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.enums.QuotationStatus;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.QuotationMapper;
import com.hardware.selvahardware.repository.QuotationRepository;
import com.hardware.selvahardware.repository.CustomerRepository;
import com.hardware.selvahardware.repository.InvoiceRepository;
import com.hardware.selvahardware.repository.ProductRepository;
import com.hardware.selvahardware.repository.PaymentRepository;
import com.hardware.selvahardware.repository.PriceMappingRepository;
import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.entity.InvoiceItem;
import com.hardware.selvahardware.entity.Payment;
import com.hardware.selvahardware.entity.PriceMapping;
import com.hardware.selvahardware.enums.InvoiceStatus;
import com.hardware.selvahardware.enums.BillType;
import com.hardware.selvahardware.enums.TransactionType;
import com.hardware.selvahardware.mapper.InvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final PriceMappingRepository priceMappingRepository;
    private final QuotationMapper quotationMapper;
    private final InvoiceMapper invoiceMapper;

    public List<QuotationDto> getAllQuotations() {
        return quotationRepository.findAllQuotationsWithItems().stream()
                .map(quotationMapper::toDto)
                .collect(Collectors.toList());
    }

    public QuotationDto getQuotationById(UUID id) {
        Quotation quotation = quotationRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));
        return quotationMapper.toDto(quotation);
    }

    public QuotationDto createQuotation(QuotationRequest request) {
        // Validate customer exists
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        // Generate quotation number
        String quotationNumber = generateQuotationNumber();

        // Create quotation
        Quotation quotation = quotationMapper.toEntity(request, customer, quotationNumber);

        // Set default validity if not provided (30 days from now)
        if (quotation.getValidUntil() == null) {
            quotation.setValidUntil(LocalDateTime.now().plusDays(30));
        }

        // Save quotation
        Quotation savedQuotation = quotationRepository.save(quotation);
        return quotationMapper.toDto(savedQuotation);
    }

    public QuotationDto updateQuotation(UUID id, QuotationRequest request) {
        Quotation existingQuotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        // Check if quotation can be updated (only pending quotations can be updated)
        if (existingQuotation.getStatus() != QuotationStatus.pending) {
            throw new BusinessException("Only pending quotations can be updated");
        }

        // Validate customer exists
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        // Update quotation details
        quotationMapper.updateEntityFromRequest(existingQuotation, request, customer);

        // Update items
        existingQuotation.getItems().clear();
        request.getItems().forEach(itemRequest -> {
            existingQuotation.getItems().add(quotationMapper.toItemEntity(itemRequest, existingQuotation));
        });

        // Recalculate totals
        recalculateQuotationTotals(existingQuotation);

        // Save updated quotation
        Quotation savedQuotation = quotationRepository.save(existingQuotation);
        return quotationMapper.toDto(savedQuotation);
    }

    public void deleteQuotation(UUID id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        // Check if quotation can be deleted (only pending or cancelled quotations can
        // be deleted)
        if (quotation.getStatus() == QuotationStatus.confirmed || quotation.getStatus() == QuotationStatus.converted) {
            throw new BusinessException("Confirmed or converted quotations cannot be deleted");
        }

        quotationRepository.deleteById(id);
    }

    public QuotationDto confirmQuotation(UUID id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        if (quotation.getStatus() != QuotationStatus.pending) {
            throw new BusinessException("Only pending quotations can be confirmed");
        }

        // Check if quotation is still valid
        if (quotation.getValidUntil() != null && quotation.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Quotation has expired and cannot be confirmed");
        }

        quotation.setStatus(QuotationStatus.confirmed);
        Quotation savedQuotation = quotationRepository.save(quotation);
        return quotationMapper.toDto(savedQuotation);
    }

    public QuotationDto cancelQuotation(UUID id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        if (quotation.getStatus() == QuotationStatus.converted) {
            throw new BusinessException("Converted quotations cannot be cancelled");
        }

        quotation.setStatus(QuotationStatus.cancelled);
        Quotation savedQuotation = quotationRepository.save(quotation);
        return quotationMapper.toDto(savedQuotation);
    }

    public List<QuotationDto> getQuotationsByCustomer(UUID customerId) {
        return quotationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(quotationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<QuotationDto> getQuotationsByStatus(QuotationStatus status) {
        return quotationRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(quotationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<QuotationDto> filterQuotations(UUID customerId, QuotationStatus status,
            LocalDateTime fromDate, LocalDateTime toDate, String search) {
        return quotationRepository.filterQuotations(customerId, status, fromDate, toDate, search).stream()
                .map(quotationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<QuotationDto> getExpiredQuotations() {
        List<Quotation> expiredQuotations = quotationRepository.findExpiredQuotations(LocalDateTime.now());
        return expiredQuotations.stream()
                .map(quotationMapper::toDto)
                .collect(Collectors.toList());
    }

    private String generateQuotationNumber() {
        String prefix = "QOT-" + LocalDateTime.now().getYear();
        String lastNumber = quotationRepository.findLastQuotationNumber(prefix)
                .orElse(prefix + "-00000");

        String[] parts = lastNumber.split("-");
        int sequence = Integer.parseInt(parts[2]) + 1;

        return String.format("%s-%05d", prefix, sequence);
    }

    private void recalculateQuotationTotals(Quotation quotation) {
        // Recalculate subtotal
        java.math.BigDecimal subtotal = quotation.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        // Recalculate total discount
        java.math.BigDecimal totalDiscount = quotation.getItems().stream()
                .map(QuotationItem::getDiscount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        // Recalculate total GST
        java.math.BigDecimal totalGst = quotation.getItems().stream()
                .map(item -> {
                    java.math.BigDecimal itemTotal = item.getUnitPrice()
                            .multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
                    java.math.BigDecimal discountedTotal = itemTotal.subtract(item.getDiscount());
                    return discountedTotal.multiply(item.getGstPercentage()).divide(java.math.BigDecimal.valueOf(100));
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        // Recalculate grand total
        java.math.BigDecimal grandTotal = subtotal.subtract(totalDiscount).add(totalGst);

        quotation.setSubtotal(subtotal);
        quotation.setTotalDiscount(totalDiscount);
        quotation.setTotalGst(totalGst);
        quotation.setGrandTotal(grandTotal);
    }

    public com.hardware.selvahardware.dto.invoice.InvoiceDto convertToInvoice(UUID id) {
        Quotation quotation = quotationRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));

        // Check if quotation can be converted (only confirmed quotations can be
        // converted)
        if (quotation.getStatus() != QuotationStatus.confirmed) {
            throw new BusinessException("Only confirmed quotations can be converted to invoice");
        }

        // Check if quotation is already converted
        if (quotation.getStatus() == QuotationStatus.converted) {
            throw new BusinessException("Quotation is already converted to invoice");
        }

        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber();

        // Create invoice from quotation
        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .customer(quotation.getCustomer())
                .customerName(quotation.getCustomerName())
                .customerPhone(quotation.getCustomerPhone())
                .customerType(quotation.getCustomerType())
                .endCustomerName(quotation.getEndCustomerName())
                .commission(quotation.getCommission())
                .subtotal(quotation.getSubtotal())
                .totalDiscount(quotation.getTotalDiscount())
                .totalGst(quotation.getTotalGst())
                .grandTotal(quotation.getGrandTotal())
                .amountPaid(java.math.BigDecimal.ZERO)
                .balance(quotation.getGrandTotal())
                .paymentMethod(com.hardware.selvahardware.enums.PaymentMethod.cash) // Default payment method
                .status(InvoiceStatus.pending)
                .billType(BillType.final_bill)
                .notes(quotation.getNotes())
                .build();

        // Convert quotation items to invoice items
        List<InvoiceItem> invoiceItems = quotation.getItems().stream()
                .map(quotationItem -> InvoiceItem.builder()
                        .productId(quotationItem.getProduct() != null ? quotationItem.getProduct().getId() : null)
                        .productName(quotationItem.getProductName())
                        .productCode(quotationItem.getProductCode())
                        .quantity(java.math.BigDecimal.valueOf(quotationItem.getQuantity()))
                        .price(quotationItem.getUnitPrice())
                        .originalPrice(quotationItem.getUnitPrice())
                        .gstPercentage(quotationItem.getGstPercentage())
                        .gstAmount(calculateGstAmount(quotationItem))
                        .discount(quotationItem.getDiscount())
                        .subtotal(quotationItem.getUnitPrice()
                                .multiply(java.math.BigDecimal.valueOf(quotationItem.getQuantity())))
                        .total(quotationItem.getTotal())
                        .build())
                .collect(Collectors.toList());

        // Set invoice items
        invoiceItems.forEach(item -> item.setInvoice(invoice));
        invoice.setItems(invoiceItems);

        // Save invoice
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Update quotation status and link to invoice
        quotation.setStatus(QuotationStatus.converted);
        quotation.setConvertedToInvoice(savedInvoice);
        quotationRepository.save(quotation);

        // Update stock for final bills
        quotation.getItems().forEach(quotationItem -> {
            if (quotationItem.getProduct() != null) {
                productRepository.findById(quotationItem.getProduct().getId()).ifPresent(product -> {
                    int newStock = product.getStock() - quotationItem.getQuantity();
                    product.setStock(Math.max(0, newStock));
                    productRepository.save(product);
                });

                // Update price mapping
                UUID customerId = quotation.getCustomer().getId();
                UUID productId = quotationItem.getProduct().getId();
                priceMappingRepository.findByCustomerIdAndProductId(customerId, productId)
                        .ifPresentOrElse(pm -> {
                            pm.setLastPrice(quotationItem.getUnitPrice());
                            priceMappingRepository.save(pm);
                        }, () -> {
                            PriceMapping pm = PriceMapping.builder()
                                    .customerId(customerId)
                                    .productId(productId)
                                    .lastPrice(quotationItem.getUnitPrice())
                                    .build();
                            priceMappingRepository.save(pm);
                        });
            }
        });

        // Record billing transaction
        java.math.BigDecimal customerBalance = getCustomerBalance(quotation.getCustomer().getId());
        Payment billingTx = Payment.builder()
                .customer(quotation.getCustomer())
                .customerName(quotation.getCustomerName())
                .invoiceId(savedInvoice.getId())
                .invoiceNumber(invoiceNumber)
                .type(TransactionType.billing)
                .amount(quotation.getGrandTotal())
                .balanceAfter(customerBalance.add(quotation.getGrandTotal()))
                .paymentMethod(com.hardware.selvahardware.enums.PaymentMethod.cash)
                .notes("Converted from quotation: " + quotation.getQuotationNumber())
                .build();
        paymentRepository.save(billingTx);

        return invoiceMapper.toDto(savedInvoice);
    }

    private String generateInvoiceNumber() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyMM");
        String yearMonth = LocalDateTime.now().format(formatter);
        String prefix = "INV";
        String lastNumber = invoiceRepository.findLastInvoiceNumber(prefix + yearMonth).orElse(null);
        int counter = 1;
        if (lastNumber != null && lastNumber.length() > prefix.length() + 4) {
            try {
                counter = Integer.parseInt(lastNumber.substring(prefix.length() + 4)) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return prefix + yearMonth + String.format("%04d", counter);
    }

    private java.math.BigDecimal calculateGstAmount(QuotationItem item) {
        java.math.BigDecimal itemTotal = item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
        java.math.BigDecimal discountedTotal = itemTotal.subtract(item.getDiscount());
        return discountedTotal.multiply(item.getGstPercentage()).divide(java.math.BigDecimal.valueOf(100));
    }

    private java.math.BigDecimal getCustomerBalance(UUID customerId) {
        List<Invoice> invoices = invoiceRepository.findByCustomerIdOrderByCreatedAtAsc(customerId);
        return invoices.stream().map(Invoice::getBalance).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
