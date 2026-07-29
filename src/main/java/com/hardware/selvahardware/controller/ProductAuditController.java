package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.product.ProductAuditDto;
import com.hardware.selvahardware.enums.AuditAction;
import com.hardware.selvahardware.service.ProductAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Audit Management")
@SecurityRequirement(name = "bearerAuth")
public class ProductAuditController {

    private final ProductAuditService productAuditService;

    @GetMapping("/{productId}/audits")
    @Operation(summary = "Get all audit logs for a product")
    public ResponseEntity<ApiResponse<List<ProductAuditDto>>> getAuditsByProductId(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(productAuditService.getAuditsByProductId(productId)));
    }

    @GetMapping("/{productId}/audits/{action}")
    @Operation(summary = "Get audit logs for a product by action type")
    public ResponseEntity<ApiResponse<List<ProductAuditDto>>> getAuditsByProductIdAndAction(
            @PathVariable UUID productId,
            @PathVariable AuditAction action) {
        return ResponseEntity.ok(ApiResponse.success(productAuditService.getAuditsByProductIdAndAction(productId, action)));
    }
}
