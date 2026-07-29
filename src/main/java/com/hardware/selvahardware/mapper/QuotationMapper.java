package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.quotation.QuotationDto;
import com.hardware.selvahardware.dto.quotation.QuotationItemDto;
import com.hardware.selvahardware.dto.quotation.QuotationRequest;
import com.hardware.selvahardware.dto.quotation.QuotationItemRequest;
import com.hardware.selvahardware.entity.Quotation;
import com.hardware.selvahardware.entity.QuotationItem;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.entity.Product;
import com.hardware.selvahardware.repository.CustomerRepository;
import com.hardware.selvahardware.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuotationMapper {

    private final ProductRepository productRepository;

    public QuotationMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public QuotationDto toDto(Quotation quotation) {
        if (quotation == null) {
            return null;
        }

        List<QuotationItemDto> itemDtos = quotation.getItems() != null
                ? quotation.getItems().stream().map(this::toItemDto).collect(Collectors.toList())
                : null;

        return QuotationDto.builder()
                .id(quotation.getId())
                .quotationNumber(quotation.getQuotationNumber())
                .customerId(quotation.getCustomer() != null ? quotation.getCustomer().getId() : null)
                .customerName(quotation.getCustomerName())
                .customerPhone(quotation.getCustomerPhone())
                .customerType(quotation.getCustomerType())
                .endCustomerName(quotation.getEndCustomerName())
                .contractorId(quotation.getContractor() != null ? quotation.getContractor().getId() : null)
                .contractorName(quotation.getContractorName())
                .commission(quotation.getCommission())
                .subtotal(quotation.getSubtotal())
                .totalDiscount(quotation.getTotalDiscount())
                .totalGst(quotation.getTotalGst())
                .grandTotal(quotation.getGrandTotal())
                .validUntil(quotation.getValidUntil())
                .status(quotation.getStatus())
                .notes(quotation.getNotes())
                .termsConditions(quotation.getTermsConditions())
                .createdAt(quotation.getCreatedAt())
                .updatedAt(quotation.getUpdatedAt())
                .convertedAt(quotation.getConvertedAt())
                .convertedToInvoiceId(
                        quotation.getConvertedToInvoice() != null ? quotation.getConvertedToInvoice().getId() : null)
                .items(itemDtos)
                .build();
    }

    public QuotationItemDto toItemDto(QuotationItem item) {
        if (item == null) {
            return null;
        }

        return QuotationItemDto.builder()
                .id(item.getId())
                .quotationId(item.getQuotation() != null ? item.getQuotation().getId() : null)
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProductName())
                .productCode(item.getProductCode())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .gstPercentage(item.getGstPercentage())
                .discount(item.getDiscount())
                .total(item.getTotal())
                .description(item.getDescription())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    public Quotation toEntity(QuotationRequest request, Customer customer, String quotationNumber) {
        if (request == null) {
            return null;
        }

        List<QuotationItem> items = request.getItems() != null
                ? request.getItems().stream()
                        .map(itemRequest -> toItemEntity(itemRequest, null))
                        .collect(Collectors.toList())
                : null;

        return Quotation.builder()
                .quotationNumber(quotationNumber)
                .customer(customer)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerType(request.getCustomerType())
                .endCustomerName(request.getEndCustomerName())
                .commission(request.getCommission())
                .subtotal(calculateSubtotal(items))
                .totalDiscount(calculateTotalDiscount(items))
                .totalGst(calculateTotalGst(items))
                .grandTotal(calculateGrandTotal(items))
                .validUntil(request.getValidUntil())
                .notes(request.getNotes())
                .termsConditions(request.getTermsConditions())
                .items(items)
                .build();
    }

    public QuotationItem toItemEntity(QuotationItemRequest request, Quotation quotation) {
        if (request == null) {
            return null;
        }

        // Fetch product entity to ensure product_id is not null
        Product product = null;
        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId()).orElse(null);
        }

        return QuotationItem.builder()
                .quotation(quotation)
                .product(product)
                .productName(request.getProductName())
                .productCode(request.getProductCode())
                .unitPrice(request.getUnitPrice())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .gstPercentage(request.getGstPercentage())
                .discount(request.getDiscount())
                .total(calculateItemTotal(request))
                .description(request.getDescription())
                .build();
    }

    private java.math.BigDecimal calculateSubtotal(List<QuotationItem> items) {
        if (items == null || items.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    private java.math.BigDecimal calculateTotalDiscount(List<QuotationItem> items) {
        if (items == null || items.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        return items.stream()
                .map(QuotationItem::getDiscount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    private java.math.BigDecimal calculateTotalGst(List<QuotationItem> items) {
        if (items == null || items.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> {
                    java.math.BigDecimal itemTotal = item.getUnitPrice()
                            .multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
                    java.math.BigDecimal discountedTotal = itemTotal.subtract(item.getDiscount());
                    return discountedTotal.multiply(item.getGstPercentage()).divide(java.math.BigDecimal.valueOf(100));
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    private java.math.BigDecimal calculateGrandTotal(List<QuotationItem> items) {
        java.math.BigDecimal subtotal = calculateSubtotal(items);
        java.math.BigDecimal totalDiscount = calculateTotalDiscount(items);
        java.math.BigDecimal totalGst = calculateTotalGst(items);
        return subtotal.subtract(totalDiscount).add(totalGst);
    }

    private java.math.BigDecimal calculateItemTotal(QuotationItemRequest request) {
        java.math.BigDecimal itemTotal = request.getUnitPrice()
                .multiply(java.math.BigDecimal.valueOf(request.getQuantity()));
        java.math.BigDecimal discountedTotal = itemTotal.subtract(request.getDiscount());
        java.math.BigDecimal gstAmount = discountedTotal.multiply(request.getGstPercentage())
                .divide(java.math.BigDecimal.valueOf(100));
        return discountedTotal.add(gstAmount);
    }

    public void updateEntityFromRequest(Quotation quotation, QuotationRequest request, Customer customer) {
        if (quotation == null || request == null) {
            return;
        }

        quotation.setCustomer(customer);
        quotation.setCustomerName(request.getCustomerName());
        quotation.setCustomerPhone(request.getCustomerPhone());
        quotation.setCustomerType(request.getCustomerType());
        quotation.setEndCustomerName(request.getEndCustomerName());
        quotation.setCommission(request.getCommission());
        quotation.setValidUntil(request.getValidUntil());
        quotation.setNotes(request.getNotes());
        quotation.setTermsConditions(request.getTermsConditions());
    }

    public Customer getContractorFromRequest(QuotationRequest request, CustomerRepository customerRepository) {
        if (request.getContractorId() != null) {
            return customerRepository.findById(request.getContractorId()).orElse(null);
        }
        return null;
    }
}
