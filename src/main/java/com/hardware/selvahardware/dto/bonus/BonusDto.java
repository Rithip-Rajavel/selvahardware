package com.hardware.selvahardware.dto.bonus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusDto {
    
    private UUID id;
    private UUID customerId;
    private String customerName;
    private BigDecimal bonusAmount;
    private Integer bonusMonth;
    private Integer bonusYear;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
