package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.invoice.InvoiceItemRequest;
import com.hardware.selvahardware.dto.pending.PendingBillDto;
import com.hardware.selvahardware.dto.pending.PendingBillRequest;
import com.hardware.selvahardware.entity.PendingBill;
import com.hardware.selvahardware.entity.PendingBillItem;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.PendingBillMapper;
import com.hardware.selvahardware.repository.PendingBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PendingBillService {

    private final PendingBillRepository pendingBillRepository;
    private final PendingBillMapper pendingBillMapper;

    public List<PendingBillDto> getAll() {
        return pendingBillRepository.findAll().stream()
                .map(pendingBillMapper::toDto).collect(Collectors.toList());
    }

    public PendingBillDto getById(UUID id) {
        return pendingBillMapper.toDto(pendingBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PendingBill", id)));
    }

    public PendingBillDto create(PendingBillRequest request) {
        UUID id = UUID.randomUUID();
        PendingBill bill = PendingBill.builder()
                .id(id)
                .customerId(request.getCustomerId())
                .customerName(request.getCustomerName())
                .customerType(request.getCustomerType())
                .paymentMethod(request.getPaymentMethod())
                .billDiscount(request.getBillDiscount())
                .amountPaid(request.getAmountPaid())
                .endCustomerName(request.getEndCustomerName())
                .commission(request.getCommission())
                .notes(request.getNotes())
                .build();

        if (request.getItems() != null) {
            List<PendingBillItem> items = mapItems(request.getItems(), bill);
            bill.setItems(items);
        }

        return pendingBillMapper.toDto(pendingBillRepository.save(bill));
    }

    public PendingBillDto update(UUID id, PendingBillRequest request) {
        PendingBill bill = pendingBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PendingBill", id));
        bill.setCustomerId(request.getCustomerId());
        bill.setCustomerName(request.getCustomerName());
        bill.setCustomerType(request.getCustomerType());
        bill.setPaymentMethod(request.getPaymentMethod());
        bill.setBillDiscount(request.getBillDiscount());
        bill.setAmountPaid(request.getAmountPaid());
        bill.setEndCustomerName(request.getEndCustomerName());
        bill.setCommission(request.getCommission());
        bill.setNotes(request.getNotes());
        if (request.getItems() != null) {
            bill.getItems().clear();
            bill.getItems().addAll(mapItems(request.getItems(), bill));
        }
        return pendingBillMapper.toDto(pendingBillRepository.save(bill));
    }

    public void delete(UUID id) {
        if (!pendingBillRepository.existsById(id)) {
            throw new ResourceNotFoundException("PendingBill", id);
        }
        pendingBillRepository.deleteById(id);
    }

    private List<PendingBillItem> mapItems(List<InvoiceItemRequest> reqs, PendingBill bill) {
        return reqs.stream().map(req -> PendingBillItem.builder()
                .id(UUID.randomUUID())
                .pendingBill(bill)
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
                .build()
        ).collect(Collectors.toList());
    }
}
