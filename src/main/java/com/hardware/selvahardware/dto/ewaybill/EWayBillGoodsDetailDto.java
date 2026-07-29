package com.hardware.selvahardware.dto.ewaybill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EWayBillGoodsDetailDto {

    private UUID id;
    private String productId;
    private String hsnCode;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal taxableAmount;
    private String taxRate;
}
