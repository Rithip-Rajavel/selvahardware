package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.commission.CommissionDto;
import com.hardware.selvahardware.dto.commission.CommissionRequest;
import com.hardware.selvahardware.entity.Commission;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.CommissionMapper;
import com.hardware.selvahardware.repository.CommissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final CommissionMapper commissionMapper;

    public List<CommissionDto> getAll(String contractorId, String fromDate, String toDate) {
        LocalDateTime from = fromDate != null ? LocalDateTime.parse(fromDate + "T00:00:00") : null;
        LocalDateTime to = toDate != null ? LocalDateTime.parse(toDate + "T23:59:59") : null;
        return commissionRepository.filterCommissions(contractorId, from, to)
                .stream().map(commissionMapper::toDto).collect(Collectors.toList());
    }

    public CommissionDto create(CommissionRequest request) {
        Commission commission = Commission.builder()
                .id(UUID.randomUUID().toString())
                .contractorId(request.getContractorId())
                .contractorName(request.getContractorName())
                .invoiceId(request.getInvoiceId())
                .invoiceNumber(request.getInvoiceNumber())
                .endCustomerName(request.getEndCustomerName())
                .amount(request.getAmount())
                .build();
        return commissionMapper.toDto(commissionRepository.save(commission));
    }

    public List<CommissionDto> getByContractor(String contractorId) {
        return commissionRepository.findByContractorIdOrderByCreatedAtDesc(contractorId)
                .stream().map(commissionMapper::toDto).collect(Collectors.toList());
    }

    public BigDecimal getTotalByContractor(String contractorId) {
        return commissionRepository.sumByContractorId(contractorId);
    }
}
