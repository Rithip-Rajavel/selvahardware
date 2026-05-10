package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.ewaybill.EWayBillDto;
import com.hardware.selvahardware.dto.ewaybill.EWayBillGoodsDetailDto;
import com.hardware.selvahardware.dto.ewaybill.EWayBillRequest;
import com.hardware.selvahardware.entity.EWayBill;
import com.hardware.selvahardware.entity.EWayBillGoodsDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EWayBillMapper {

    public EWayBillDto toDto(EWayBill entity) {
        if (entity == null) {
            return null;
        }

        List<EWayBillGoodsDetailDto> goodsDetailDtos = null;
        if (entity.getGoodsDetails() != null) {
            goodsDetailDtos = entity.getGoodsDetails().stream()
                    .map(this::toGoodsDetailDto)
                    .collect(Collectors.toList());
        }

        return EWayBillDto.builder()
                .id(entity.getId())
                .invoiceId(entity.getInvoice() != null ? entity.getInvoice().getId() : null)
                .ewayBillNo(entity.getEwayBillNo())
                .generatedDate(entity.getGeneratedDate())
                .generatedBy(entity.getGeneratedBy())
                .validUpto(entity.getValidUpto())
                .portal(entity.getPortal())
                .mode(entity.getMode())
                .approxDistance(entity.getApproxDistance())
                .type(entity.getType())
                .documentType(entity.getDocumentType())
                .documentNumber(entity.getDocumentNumber())
                .documentDate(entity.getDocumentDate())
                .transactionType(entity.getTransactionType())
                .fromGstin(entity.getFromGstin())
                .fromName(entity.getFromName())
                .fromAddress(entity.getFromAddress())
                .fromPlace(entity.getFromPlace())
                .fromPincode(entity.getFromPincode())
                .fromState(entity.getFromState())
                .toGstin(entity.getToGstin())
                .toName(entity.getToName())
                .toAddress(entity.getToAddress())
                .transporterId(entity.getTransporterId())
                .transporterName(entity.getTransporterName())
                .transporterDocNo(entity.getTransporterDocNo())
                .transporterDocDate(entity.getTransporterDocDate())
                .vehicleNo(entity.getVehicleNo())
                .vehicleType(entity.getVehicleType())
                .enteredDate(entity.getEnteredDate())
                .enteredBy(entity.getEnteredBy())
                .cewbNo(entity.getCewbNo())
                .multiVehInfo(entity.getMultiVehInfo())
                .goodsDetails(goodsDetailDtos)
                .taxableAmount(entity.getTaxableAmount())
                .cgstAmount(entity.getCgstAmount())
                .sgstAmount(entity.getSgstAmount())
                .igstAmount(entity.getIgstAmount())
                .cessAmount(entity.getCessAmount())
                .cessNonAdvolAmount(entity.getCessNonAdvolAmount())
                .otherAmount(entity.getOtherAmount())
                .totalInvoiceAmount(entity.getTotalInvoiceAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public EWayBill toEntity(EWayBillRequest request) {
        if (request == null) {
            return null;
        }

        return EWayBill.builder()
                .ewayBillNo(request.getEwayBillNo())
                .generatedDate(request.getGeneratedDate())
                .generatedBy(request.getGeneratedBy())
                .validUpto(request.getValidUpto())
                .portal(request.getPortal())
                .mode(request.getMode())
                .approxDistance(request.getApproxDistance())
                .type(request.getType())
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .documentDate(request.getDocumentDate())
                .transactionType(request.getTransactionType())
                .fromGstin(request.getFromGstin())
                .fromName(request.getFromName())
                .fromAddress(request.getFromAddress())
                .fromPlace(request.getFromPlace())
                .fromPincode(request.getFromPincode())
                .fromState(request.getFromState())
                .toGstin(request.getToGstin())
                .toName(request.getToName())
                .toAddress(request.getToAddress())
                .transporterId(request.getTransporterId())
                .transporterName(request.getTransporterName())
                .transporterDocNo(request.getTransporterDocNo())
                .transporterDocDate(request.getTransporterDocDate())
                .vehicleNo(request.getVehicleNo())
                .vehicleType(request.getVehicleType())
                .enteredDate(request.getEnteredDate())
                .enteredBy(request.getEnteredBy())
                .cewbNo(request.getCewbNo())
                .multiVehInfo(request.getMultiVehInfo())
                .taxableAmount(request.getTaxableAmount())
                .cgstAmount(request.getCgstAmount())
                .sgstAmount(request.getSgstAmount())
                .igstAmount(request.getIgstAmount())
                .cessAmount(request.getCessAmount())
                .cessNonAdvolAmount(request.getCessNonAdvolAmount())
                .otherAmount(request.getOtherAmount())
                .totalInvoiceAmount(request.getTotalInvoiceAmount())
                .build();
    }

    public void updateEntity(EWayBill entity, EWayBillRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setEwayBillNo(request.getEwayBillNo());
        entity.setGeneratedDate(request.getGeneratedDate());
        entity.setGeneratedBy(request.getGeneratedBy());
        entity.setValidUpto(request.getValidUpto());
        entity.setPortal(request.getPortal());
        entity.setMode(request.getMode());
        entity.setApproxDistance(request.getApproxDistance());
        entity.setType(request.getType());
        entity.setDocumentType(request.getDocumentType());
        entity.setDocumentNumber(request.getDocumentNumber());
        entity.setDocumentDate(request.getDocumentDate());
        entity.setTransactionType(request.getTransactionType());
        entity.setFromGstin(request.getFromGstin());
        entity.setFromName(request.getFromName());
        entity.setFromAddress(request.getFromAddress());
        entity.setFromPlace(request.getFromPlace());
        entity.setFromPincode(request.getFromPincode());
        entity.setFromState(request.getFromState());
        entity.setToGstin(request.getToGstin());
        entity.setToName(request.getToName());
        entity.setToAddress(request.getToAddress());
        entity.setTransporterId(request.getTransporterId());
        entity.setTransporterName(request.getTransporterName());
        entity.setTransporterDocNo(request.getTransporterDocNo());
        entity.setTransporterDocDate(request.getTransporterDocDate());
        entity.setVehicleNo(request.getVehicleNo());
        entity.setVehicleType(request.getVehicleType());
        entity.setEnteredDate(request.getEnteredDate());
        entity.setEnteredBy(request.getEnteredBy());
        entity.setCewbNo(request.getCewbNo());
        entity.setMultiVehInfo(request.getMultiVehInfo());
        entity.setTaxableAmount(request.getTaxableAmount());
        entity.setCgstAmount(request.getCgstAmount());
        entity.setSgstAmount(request.getSgstAmount());
        entity.setIgstAmount(request.getIgstAmount());
        entity.setCessAmount(request.getCessAmount());
        entity.setCessNonAdvolAmount(request.getCessNonAdvolAmount());
        entity.setOtherAmount(request.getOtherAmount());
        entity.setTotalInvoiceAmount(request.getTotalInvoiceAmount());
    }

    public EWayBillGoodsDetailDto toGoodsDetailDto(EWayBillGoodsDetail entity) {
        if (entity == null) {
            return null;
        }

        return EWayBillGoodsDetailDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .hsnCode(entity.getHsnCode())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .taxableAmount(entity.getTaxableAmount())
                .taxRate(entity.getTaxRate())
                .build();
    }

    public EWayBillGoodsDetail toGoodsDetailEntity(EWayBillGoodsDetailDto dto, EWayBill eWayBill) {
        if (dto == null) {
            return null;
        }

        return EWayBillGoodsDetail.builder()
                .productId(dto.getProductId())
                .hsnCode(dto.getHsnCode())
                .productName(dto.getProductName())
                .quantity(dto.getQuantity())
                .taxableAmount(dto.getTaxableAmount())
                .taxRate(dto.getTaxRate())
                .ewayBill(eWayBill)
                .build();
    }
}
