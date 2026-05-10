package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.ewaybill.EWayBillDto;
import com.hardware.selvahardware.dto.ewaybill.EWayBillGoodsDetailDto;
import com.hardware.selvahardware.dto.ewaybill.EWayBillRequest;
import com.hardware.selvahardware.entity.EWayBill;
import com.hardware.selvahardware.entity.EWayBillGoodsDetail;
import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.entity.ShopSettings;
import com.hardware.selvahardware.enums.EWayBillStatus;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.EWayBillMapper;
import com.hardware.selvahardware.repository.*;
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
public class EWayBillService {

    private final EWayBillRepository ewayBillRepository;
    private final EWayBillGoodsDetailRepository ewayBillGoodsDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final ShopSettingsRepository shopSettingsRepository;
    private final EWayBillMapper ewayBillMapper;

    public List<EWayBillDto> getAllEWayBills() {
        return ewayBillRepository.findAll().stream()
                .map(ewayBillMapper::toDto)
                .collect(Collectors.toList());
    }

    public EWayBillDto getEWayBillById(UUID id) {
        EWayBill ewayBill = ewayBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("E-Way Bill not found with id: " + id));
        return ewayBillMapper.toDto(ewayBill);
    }

    public EWayBillDto getEWayBillByNumber(String ewayBillNo) {
        EWayBill ewayBill = ewayBillRepository.findByEwayBillNo(ewayBillNo)
                .orElseThrow(() -> new ResourceNotFoundException("E-Way Bill not found with number: " + ewayBillNo));
        return ewayBillMapper.toDto(ewayBill);
    }

    public List<EWayBillDto> getEWayBillsByInvoiceId(UUID invoiceId) {
        return ewayBillRepository.findByInvoiceId(invoiceId).stream()
                .map(ewayBillMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EWayBillDto> getEWayBillsByStatus(EWayBillStatus status) {
        return ewayBillRepository.findByStatus(status).stream()
                .map(ewayBillMapper::toDto)
                .collect(Collectors.toList());
    }

    public EWayBillDto createEWayBill(EWayBillRequest request) {
        // Validate invoice exists and has GST enabled
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

        // Check if E-Way Bill already exists for this invoice
        if (ewayBillRepository.findByInvoiceId(request.getInvoiceId()).stream()
                .anyMatch(ewb -> ewb.getStatus() != EWayBillStatus.cancelled)) {
            throw new BusinessException("E-Way Bill already exists for this invoice");
        }

        // Get shop settings for auto-filled fields
        ShopSettings shopSettings = shopSettingsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("Shop settings not configured"));

        EWayBill ewayBill = ewayBillMapper.toEntity(request);
        ewayBill.setInvoice(invoice);

        // Auto-fill supplier details from shop settings
        ewayBill.setFromGstin(shopSettings.getGstNumber());
        ewayBill.setFromName(shopSettings.getName());
        ewayBill.setFromAddress(shopSettings.getAddress());

        // Auto-fill document details from invoice
        ewayBill.setDocumentNumber(invoice.getInvoiceNumber());
        ewayBill.setDocumentDate(invoice.getCreatedAt().toLocalDate());

        // Auto-fill recipient details from invoice customer
        ewayBill.setToName(invoice.getCustomerName());

        // Set generated date and entered date
        LocalDateTime now = LocalDateTime.now();
        ewayBill.setGeneratedDate(now);
        ewayBill.setEnteredDate(now);
        ewayBill.setGeneratedBy(shopSettings.getGstNumber());
        ewayBill.setEnteredBy(shopSettings.getGstNumber());

        // Calculate amounts from invoice
        ewayBill.setTaxableAmount(invoice.getSubtotal());
        ewayBill.setCgstAmount(invoice.getTotalGst().divide(java.math.BigDecimal.valueOf(2)));
        ewayBill.setSgstAmount(invoice.getTotalGst().divide(java.math.BigDecimal.valueOf(2)));
        ewayBill.setIgstAmount(java.math.BigDecimal.ZERO);
        ewayBill.setTotalInvoiceAmount(invoice.getGrandTotal());

        // Save E-Way Bill
        EWayBill savedEWayBill = ewayBillRepository.save(ewayBill);

        // Save goods details
        if (request.getGoodsDetails() != null) {
            List<EWayBillGoodsDetail> goodsDetails = request.getGoodsDetails().stream()
                    .map(dto -> ewayBillMapper.toGoodsDetailEntity(dto, savedEWayBill))
                    .collect(Collectors.toList());

            ewayBillGoodsDetailRepository.saveAll(goodsDetails);
            savedEWayBill.setGoodsDetails(goodsDetails);
        }

        return ewayBillMapper.toDto(savedEWayBill);
    }

    public EWayBillDto updateEWayBill(UUID id, EWayBillRequest request) {
        EWayBill existingEWayBill = ewayBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("E-Way Bill not found with id: " + id));

        if (existingEWayBill.getStatus() == EWayBillStatus.generated) {
            throw new BusinessException("Cannot update generated E-Way Bill");
        }

        // Update fields
        ewayBillMapper.updateEntity(existingEWayBill, request);

        // Update goods details
        if (request.getGoodsDetails() != null) {
            // Remove existing goods details
            ewayBillGoodsDetailRepository.deleteByEwayBillId(id);

            // Add new goods details
            List<EWayBillGoodsDetail> goodsDetails = request.getGoodsDetails().stream()
                    .map(dto -> ewayBillMapper.toGoodsDetailEntity(dto, existingEWayBill))
                    .collect(Collectors.toList());

            ewayBillGoodsDetailRepository.saveAll(goodsDetails);
            existingEWayBill.setGoodsDetails(goodsDetails);
        }

        EWayBill updatedEWayBill = ewayBillRepository.save(existingEWayBill);
        return ewayBillMapper.toDto(updatedEWayBill);
    }

    public EWayBillDto generateEWayBill(UUID id) {
        EWayBill ewayBill = ewayBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("E-Way Bill not found with id: " + id));

        if (ewayBill.getStatus() != EWayBillStatus.draft) {
            throw new BusinessException("Only draft E-Way Bills can be generated");
        }

        // Generate unique 12-digit E-Way Bill number
        String ewayBillNo = generateEWayBillNumber();

        // Check for uniqueness
        if (ewayBillRepository.existsByEwayBillNo(ewayBillNo)) {
            throw new BusinessException("Failed to generate unique E-Way Bill number");
        }

        ewayBill.setEwayBillNo(ewayBillNo);
        ewayBill.setStatus(EWayBillStatus.generated);
        ewayBill.setValidUpto(LocalDateTime.now().plusDays(1).toLocalDate());

        EWayBill generatedEWayBill = ewayBillRepository.save(ewayBill);
        return ewayBillMapper.toDto(generatedEWayBill);
    }

    public EWayBillDto cancelEWayBill(UUID id) {
        EWayBill ewayBill = ewayBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("E-Way Bill not found with id: " + id));

        if (ewayBill.getStatus() != EWayBillStatus.generated) {
            throw new BusinessException("Only generated E-Way Bills can be cancelled");
        }

        ewayBill.setStatus(EWayBillStatus.cancelled);
        EWayBill cancelledEWayBill = ewayBillRepository.save(ewayBill);
        return ewayBillMapper.toDto(cancelledEWayBill);
    }

    public void deleteEWayBill(UUID id) {
        EWayBill ewayBill = ewayBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("E-Way Bill not found with id: " + id));

        if (ewayBill.getStatus() == EWayBillStatus.generated) {
            throw new BusinessException("Cannot delete generated E-Way Bill");
        }

        ewayBillRepository.delete(ewayBill);
    }

    public List<EWayBillDto> getEWayBillsByDateRange(String fromDate, String toDate) {
        LocalDateTime start = LocalDateTime.parse(fromDate + "T00:00:00");
        LocalDateTime end = LocalDateTime.parse(toDate + "T23:59:59");

        return ewayBillRepository.findByGeneratedDateBetween(start, end).stream()
                .map(ewayBillMapper::toDto)
                .collect(Collectors.toList());
    }

    public long getEWayBillCountByStatus(EWayBillStatus status) {
        return ewayBillRepository.countByStatus(status);
    }

    private String generateEWayBillNumber() {
        // Generate 12-digit random number
        return String.format("%012d", (long) (Math.random() * 1000000000000L));
    }

    public EWayBillDto createEWayBillFromInvoice(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        EWayBillRequest request = new EWayBillRequest();
        request.setInvoiceId(invoiceId);

        // Set default values
        request.setMode("road");
        request.setType("outward_supply");
        request.setDocumentType("tax_invoice");
        request.setTransactionType("regular");
        request.setVehicleType("R");

        // Convert invoice items to goods details
        if (invoice.getItems() != null) {
            List<EWayBillGoodsDetailDto> goodsDetailDtos = invoice.getItems()
                    .stream()
                    .map(item -> {
                        EWayBillGoodsDetailDto detail = EWayBillGoodsDetailDto.builder()
                                .productId(item.getProductId().toString())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .taxableAmount(item.getTotal())
                                .build();
                        return detail;
                    })
                    .collect(Collectors.toList());

            request.setGoodsDetails(goodsDetailDtos);
        }

        return createEWayBill(request);
    }
}
