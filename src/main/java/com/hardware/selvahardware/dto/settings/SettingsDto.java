package com.hardware.selvahardware.dto.settings;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SettingsDto {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String gstNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String upiId;
    private BigDecimal defaultGstPercentage;
    private String invoicePrefix;
    private String termsAndConditions;
    private LocalDateTime updatedAt;
}
