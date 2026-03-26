package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.pending.PendingBillDto;
import com.hardware.selvahardware.dto.invoice.InvoiceDto;
import com.hardware.selvahardware.entity.PendingBill;
import com.hardware.selvahardware.entity.PendingBillItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PendingBillMapper {

    public PendingBillDto toDto(PendingBill bill) {
        List<InvoiceDto.InvoiceItemDto> itemDtos = null;
        if (bill.getItems() != null) {
            itemDtos = bill.getItems().stream().map(this::toItemDto).collect(Collectors.toList());
        }
        return PendingBillDto.builder()
                .id(bill.getId())
                .customerId(bill.getCustomerId())
                .customerName(bill.getCustomerName())
                .customerType(bill.getCustomerType())
                .paymentMethod(bill.getPaymentMethod())
                .billDiscount(bill.getBillDiscount())
                .amountPaid(bill.getAmountPaid())
                .endCustomerName(bill.getEndCustomerName())
                .commission(bill.getCommission())
                .notes(bill.getNotes())
                .items(itemDtos)
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }

    private InvoiceDto.InvoiceItemDto toItemDto(PendingBillItem item) {
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
}
