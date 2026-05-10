package com.hardware.selvahardware.dto.gstbill;

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
public class GstBillItemDto {

    private UUID id;
    private Integer itemNumber;
    private String particulars;
    private String hsnCode;
    private BigDecimal quantity;
    private BigDecimal rate;
    private BigDecimal total;
}
