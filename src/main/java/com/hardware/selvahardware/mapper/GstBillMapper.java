package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.gstbill.GstBillDto;
import com.hardware.selvahardware.dto.gstbill.GstBillItemDto;
import com.hardware.selvahardware.dto.gstbill.GstBillRequest;
import com.hardware.selvahardware.dto.gstbill.GstBillItemRequest;
import com.hardware.selvahardware.entity.GstBill;
import com.hardware.selvahardware.entity.GstBillItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GstBillMapper {

    public GstBillDto toDto(GstBill gstBill) {
        if (gstBill == null) {
            return null;
        }

        return GstBillDto.builder()
                .id(gstBill.getId())
                .invoiceId(gstBill.getInvoice() != null ? gstBill.getInvoice().getId() : null)
                .customerName(gstBill.getCustomerName())
                .customerAddress(gstBill.getCustomerAddress())
                .invoiceNumber(gstBill.getInvoiceNumber())
                .invoiceDate(gstBill.getInvoiceDate())
                .subtotal(gstBill.getSubtotal())
                .sgstAmount(gstBill.getSgstAmount())
                .cgstAmount(gstBill.getCgstAmount())
                .igstAmount(gstBill.getIgstAmount())
                .grandTotal(gstBill.getGrandTotal())
                .amountInWords(gstBill.getAmountInWords())
                .items(gstBill.getItems() != null ? gstBill.getItems().stream()
                        .map(this::toItemDto)
                        .collect(Collectors.toList()) : null)
                .createdAt(gstBill.getCreatedAt())
                .updatedAt(gstBill.getUpdatedAt())
                .build();
    }

    public GstBillItemDto toItemDto(GstBillItem item) {
        if (item == null) {
            return null;
        }

        return GstBillItemDto.builder()
                .id(item.getId())
                .itemNumber(item.getItemNumber())
                .particulars(item.getParticulars())
                .hsnCode(item.getHsnCode())
                .quantity(item.getQuantity())
                .rate(item.getRate())
                .total(item.getTotal())
                .build();
    }

    public GstBill toEntity(GstBillRequest request) {
        if (request == null) {
            return null;
        }

        GstBill gstBill = GstBill.builder()
                .customerName(request.getCustomerName())
                .customerAddress(request.getCustomerAddress())
                .invoiceNumber(request.getInvoiceNumber())
                .invoiceDate(request.getInvoiceDate())
                .subtotal(request.getSubtotal())
                .sgstAmount(request.getSgstAmount())
                .cgstAmount(request.getCgstAmount())
                .igstAmount(request.getIgstAmount() != null ? request.getIgstAmount() : java.math.BigDecimal.ZERO)
                .grandTotal(request.getGrandTotal())
                .amountInWords(request.getAmountInWords())
                .build();

        if (request.getItems() != null) {
            List<GstBillItem> items = request.getItems().stream()
                    .map(itemRequest -> toItemEntity(itemRequest, gstBill))
                    .collect(Collectors.toList());
            gstBill.setItems(items);
        }

        return gstBill;
    }

    public GstBillItem toItemEntity(GstBillItemRequest request, GstBill gstBill) {
        if (request == null) {
            return null;
        }

        return GstBillItem.builder()
                .itemNumber(request.getItemNumber())
                .particulars(request.getParticulars())
                .hsnCode(request.getHsnCode())
                .quantity(request.getQuantity())
                .rate(request.getRate())
                .total(request.getTotal())
                .gstBill(gstBill)
                .build();
    }

    public void updateEntity(GstBill existing, GstBillRequest request) {
        if (existing == null || request == null) {
            return;
        }

        existing.setCustomerName(request.getCustomerName());
        existing.setCustomerAddress(request.getCustomerAddress());
        existing.setInvoiceNumber(request.getInvoiceNumber());
        existing.setInvoiceDate(request.getInvoiceDate());
        existing.setSubtotal(request.getSubtotal());
        existing.setSgstAmount(request.getSgstAmount());
        existing.setCgstAmount(request.getCgstAmount());
        existing.setIgstAmount(request.getIgstAmount() != null ? request.getIgstAmount() : java.math.BigDecimal.ZERO);
        existing.setGrandTotal(request.getGrandTotal());
        existing.setAmountInWords(request.getAmountInWords());

        // Update items
        if (request.getItems() != null) {
            // Remove existing items
            if (existing.getItems() != null) {
                existing.getItems().clear();
            }

            // Add new items
            List<GstBillItem> items = request.getItems().stream()
                    .map(itemRequest -> toItemEntity(itemRequest, existing))
                    .collect(Collectors.toList());
            existing.setItems(items);
        }
    }

    public List<GstBillDto> toDtoList(List<GstBill> gstBills) {
        if (gstBills == null) {
            return null;
        }

        return gstBills.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
