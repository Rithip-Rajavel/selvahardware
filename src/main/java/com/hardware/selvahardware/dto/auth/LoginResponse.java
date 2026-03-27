package com.hardware.selvahardware.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LoginResponse {
    private boolean success;
    private UserDto user;
    private String token;
    private List<String> permissions;

    @Data
    @Builder
    public static class UserDto {
        private UUID id;
        private String name;
        private String email;
        private String role;
    }
}
