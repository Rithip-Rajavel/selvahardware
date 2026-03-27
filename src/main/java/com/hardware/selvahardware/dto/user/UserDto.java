package com.hardware.selvahardware.dto.user;

import com.hardware.selvahardware.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
