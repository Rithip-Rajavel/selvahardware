package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.commission.CommissionDto;
import com.hardware.selvahardware.entity.Commission;
import org.springframework.stereotype.Component;

@Component
public class CommissionMapper {

    public CommissionDto toDto(Commission commission) {
        return CommissionDto.builder()
                .id(commission.getId())
                .contractorId(commission.getContractorId())
                .contractorName(commission.getContractorName())
                .invoiceId(commission.getInvoiceId())
                .invoiceNumber(commission.getInvoiceNumber())
                .endCustomerName(commission.getEndCustomerName())
                .amount(commission.getAmount())
                .createdAt(commission.getCreatedAt())
                .build();
    }
}
