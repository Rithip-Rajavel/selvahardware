package com.hardware.selvahardware.dto.customer;

import com.hardware.selvahardware.enums.CustomerType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerDto {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private CustomerType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
