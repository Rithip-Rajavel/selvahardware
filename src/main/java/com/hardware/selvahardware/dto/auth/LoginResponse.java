package com.hardware.selvahardware.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

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
        private String id;
        private String name;
        private String email;
        private String role;
    }
}
