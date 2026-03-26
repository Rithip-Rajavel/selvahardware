package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.pricing.PriceMappingDto;
import com.hardware.selvahardware.entity.PriceMapping;
import org.springframework.stereotype.Component;

@Component
public class PriceMappingMapper {

    public PriceMappingDto toDto(PriceMapping pm) {
        return PriceMappingDto.builder()
                .id(pm.getId())
                .customerId(pm.getCustomerId())
                .productId(pm.getProductId())
                .lastPrice(pm.getLastPrice())
                .updatedAt(pm.getUpdatedAt())
                .build();
    }
}
