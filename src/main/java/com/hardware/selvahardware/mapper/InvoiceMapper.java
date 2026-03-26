package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.invoice.InvoiceDto;
import com.hardware.selvahardware.dto.invoice.InvoiceItemRequest;
import com.hardware.selvahardware.dto.invoice.InvoiceRequest;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.entity.InvoiceItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    public InvoiceDto toDto(Invoice invoice) {
        List<InvoiceDto.InvoiceItemDto> itemDtos = null;
        if (invoice.getItems() != null) {
            itemDtos = invoice.getItems().stream().map(this::toItemDto).collect(Collectors.toList());
        }
        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerId(invoice.getCustomer().getId())
                .customerName(invoice.getCustomerName())
                .customerPhone(invoice.getCustomerPhone())
                .customerType(invoice.getCustomerType())
                .endCustomerName(invoice.getEndCustomerName())
                .commission(invoice.getCommission())
                .subtotal(invoice.getSubtotal())
                .totalDiscount(invoice.getTotalDiscount())
                .totalGst(invoice.getTotalGst())
                .grandTotal(invoice.getGrandTotal())
                .amountPaid(invoice.getAmountPaid())
                .balance(invoice.getBalance())
                .paymentMethod(invoice.getPaymentMethod())
                .status(invoice.getStatus())
                .billType(invoice.getBillType())
                .notes(invoice.getNotes())
                .items(itemDtos)
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }

    public InvoiceDto.InvoiceItemDto toItemDto(InvoiceItem item) {
        return InvoiceDto.InvoiceItemDto.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productCode(item.getProductCode())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .originalPrice(item.getOriginalPrice())
                .lastPurchasedPrice(item.getLastPurchasedPrice())
                .gstPercentage(item.getGstPercentage())
                .withTax(item.isWithTax())
                .gstAmount(item.getGstAmount())
                .discount(item.getDiscount())
                .discountPercentage(item.getDiscountPercentage())
                .subtotal(item.getSubtotal())
                .total(item.getTotal())
                .customTotal(item.getCustomTotal())
                .pricingType(item.getPricingType())
                .height(item.getHeight())
                .width(item.getWidth())
                .area(item.getArea())
                .build();
    }

    public InvoiceItem toItemEntity(InvoiceItemRequest req, String itemId) {
        return InvoiceItem.builder()
                .id(itemId)
                .productId(req.getProductId())
                .productName(req.getProductName())
                .productCode(req.getProductCode())
                .quantity(req.getQuantity())
                .price(req.getPrice())
                .originalPrice(req.getOriginalPrice())
                .lastPurchasedPrice(req.getLastPurchasedPrice())
                .gstPercentage(req.getGstPercentage())
                .withTax(req.isWithTax())
                .gstAmount(req.getGstAmount())
                .discount(req.getDiscount())
                .discountPercentage(req.getDiscountPercentage())
                .subtotal(req.getSubtotal())
                .total(req.getTotal())
                .customTotal(req.getCustomTotal())
                .pricingType(req.getPricingType())
                .height(req.getHeight())
                .width(req.getWidth())
                .area(req.getArea())
                .build();
    }
}
