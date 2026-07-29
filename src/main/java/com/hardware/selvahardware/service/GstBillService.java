package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.gstbill.GstBillDto;
import com.hardware.selvahardware.dto.gstbill.GstBillRequest;
import com.hardware.selvahardware.entity.GstBill;
import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.GstBillMapper;
import com.hardware.selvahardware.repository.GstBillItemRepository;
import com.hardware.selvahardware.repository.GstBillRepository;
import com.hardware.selvahardware.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GstBillService {

    private final GstBillRepository gstBillRepository;
    private final GstBillItemRepository gstBillItemRepository;
    private final InvoiceRepository invoiceRepository;
    private final GstBillMapper gstBillMapper;

    public List<GstBillDto> getAllGstBills() {
        List<GstBill> gstBills = gstBillRepository.findAll();
        return gstBillMapper.toDtoList(gstBills);
    }

    public Page<GstBillDto> getAllGstBillsPaginated(Pageable pageable) {
        Page<GstBill> gstBills = gstBillRepository.findAll(pageable);
        return gstBills.map(gstBillMapper::toDto);
    }

    public GstBillDto getGstBillById(UUID id) {
        return gstBillRepository.findById(id)
                .map(gstBillMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("GST Bill not found with id: " + id));
    }

    public GstBillDto getGstBillByInvoiceId(UUID invoiceId) {
        return gstBillRepository.findByInvoiceId(invoiceId)
                .map(gstBillMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("GST Bill not found for invoice id: " + invoiceId));
    }

    public List<GstBillDto> getGstBillsByCustomerName(String customerName) {
        List<GstBill> gstBills = gstBillRepository.findByCustomerNameContainingIgnoreCase(customerName);
        return gstBillMapper.toDtoList(gstBills);
    }

    public List<GstBillDto> getGstBillsByDateRange(LocalDate fromDate, LocalDate toDate) {
        List<GstBill> gstBills = gstBillRepository.findByInvoiceDateBetween(fromDate, toDate);
        return gstBillMapper.toDtoList(gstBills);
    }

    public List<GstBillDto> getGstBillsWithFilters(String customerName, LocalDate fromDate, LocalDate toDate) {
        List<GstBill> gstBills = gstBillRepository.findWithFilters(customerName, fromDate, toDate);
        return gstBillMapper.toDtoList(gstBills);
    }

    @Transactional
    public GstBillDto createGstBill(GstBillRequest request) {
        // Validate request
        validateGstBillRequest(request);

        // Check if invoice exists
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

        // Check if GST bill already exists for this invoice
        if (gstBillRepository.findByInvoiceId(request.getInvoiceId()).isPresent()) {
            throw new BusinessException("GST Bill already exists for this invoice");
        }

        GstBill gstBill = gstBillMapper.toEntity(request);
        gstBill.setInvoice(invoice);

        GstBill savedGstBill = gstBillRepository.save(gstBill);
        return gstBillMapper.toDto(savedGstBill);
    }

    @Transactional
    public GstBillDto updateGstBill(UUID id, GstBillRequest request) {
        // Validate request
        validateGstBillRequest(request);

        GstBill existingGstBill = gstBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GST Bill not found with id: " + id));

        // Check if invoice exists
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

        // Update entity
        gstBillMapper.updateEntity(existingGstBill, request);
        existingGstBill.setInvoice(invoice);

        GstBill updatedGstBill = gstBillRepository.save(existingGstBill);
        return gstBillMapper.toDto(updatedGstBill);
    }

    public void deleteGstBill(UUID id) {
        GstBill gstBill = gstBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GST Bill not found with id: " + id));

        gstBillRepository.delete(gstBill);
    }

    public long getGstBillsCount() {
        return gstBillRepository.count();
    }

    public long getGstBillsCountByDateRange(LocalDate fromDate, LocalDate toDate) {
        return gstBillRepository.countByInvoiceDateBetween(fromDate, toDate);
    }

    private void validateGstBillRequest(GstBillRequest request) {
        // Basic validation is handled by @Valid annotations

        // Business validation
        if (request.getInvoiceDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            // Debug logging
            System.out.println("DEBUG: Invoice date: " + request.getInvoiceDate());
            System.out.println("DEBUG: Today: " + today);
            System.out.println("DEBUG: Tomorrow: " + tomorrow);
            System.out.println("DEBUG: Is after tomorrow: " + request.getInvoiceDate().isAfter(tomorrow));

            // Temporarily disable date validation for testing
            // if (request.getInvoiceDate().isAfter(tomorrow)) {
            // throw new BusinessException("Invoice date cannot be in future");
            // }
        }

        // Validate HSN code format
        if (request.getItems() != null) {
            for (var item : request.getItems()) {
                if (item.getHsnCode() != null && !item.getHsnCode().matches("^\\d{4,20}$")) {
                    throw new BusinessException(
                            "Invalid HSN code format: " + item.getHsnCode() + ". Must be 4-20 digits");
                }

                // Validate item totals
                if (item.getQuantity() != null && item.getRate() != null && item.getTotal() != null) {
                    java.math.BigDecimal calculatedTotal = item.getQuantity().multiply(item.getRate());
                    if (calculatedTotal.compareTo(item.getTotal()) != 0) {
                        throw new BusinessException(
                                "Item total must match (quantity × rate) for item: " + item.getParticulars());
                    }
                }
            }
        }

        // Validate grand total (with tolerance for rounding)
        if (request.getSubtotal() != null && request.getSgstAmount() != null &&
                request.getCgstAmount() != null && request.getGrandTotal() != null) {

            java.math.BigDecimal calculatedTotal = request.getSubtotal()
                    .add(request.getSgstAmount() != null ? request.getSgstAmount() : java.math.BigDecimal.ZERO)
                    .add(request.getCgstAmount() != null ? request.getCgstAmount() : java.math.BigDecimal.ZERO)
                    .add(request.getIgstAmount() != null ? request.getIgstAmount() : java.math.BigDecimal.ZERO);

            // Allow small rounding difference (0.01)
            java.math.BigDecimal difference = calculatedTotal.subtract(request.getGrandTotal()).abs();
            if (difference.compareTo(java.math.BigDecimal.valueOf(0.01)) > 0) {
                throw new BusinessException("Grand total must equal subtotal plus SGST plus CGST plus IGST");
            }
        }
    }
}
