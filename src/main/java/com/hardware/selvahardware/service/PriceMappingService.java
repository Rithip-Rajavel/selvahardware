package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.pricing.PriceMappingDto;
import com.hardware.selvahardware.dto.pricing.PriceMappingRequest;
import com.hardware.selvahardware.entity.PriceMapping;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.PriceMappingMapper;
import com.hardware.selvahardware.repository.PriceMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceMappingService {

    private final PriceMappingRepository priceMappingRepository;
    private final PriceMappingMapper priceMappingMapper;

    public List<PriceMappingDto> getByCustomer(UUID customerId) {
        return priceMappingRepository.findByCustomerId(customerId)
                .stream().map(priceMappingMapper::toDto).collect(Collectors.toList());
    }

    public PriceMappingDto createOrUpdate(PriceMappingRequest request) {
        PriceMapping pm = priceMappingRepository
                .findByCustomerIdAndProductId(request.getCustomerId(), request.getProductId())
                .orElse(PriceMapping.builder().build());
        pm.setCustomerId(request.getCustomerId());
        pm.setProductId(request.getProductId());
        pm.setLastPrice(request.getLastPrice());
        return priceMappingMapper.toDto(priceMappingRepository.save(pm));
    }

    public PriceMappingDto update(UUID id, PriceMappingRequest request) {
        PriceMapping pm = priceMappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PriceMapping", id));
        pm.setLastPrice(request.getLastPrice());
        return priceMappingMapper.toDto(priceMappingRepository.save(pm));
    }

    public void delete(UUID id) {
        if (!priceMappingRepository.existsById(id)) {
            throw new ResourceNotFoundException("PriceMapping", id);
        }
        priceMappingRepository.deleteById(id);
    }
}
