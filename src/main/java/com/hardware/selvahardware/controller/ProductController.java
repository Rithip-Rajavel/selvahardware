package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.product.ProductDto;
import com.hardware.selvahardware.dto.product.ProductRequest;
import com.hardware.selvahardware.dto.product.StockUpdateRequest;
import com.hardware.selvahardware.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products with optional filter")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean lowStock) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(category, search, lowStock)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search product by code or barcode")
    public ResponseEntity<ApiResponse<ProductDto>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchByCodeOrBarcode(q)));
    }

    @PostMapping
    @Operation(summary = "Add new product")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productService.createProduct(request), "Product created"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product details")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(@PathVariable String id,
                                                                  @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted"));
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Update product stock (add or subtract)")
    public ResponseEntity<ApiResponse<ProductDto>> updateStock(@PathVariable String id,
                                                                @Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateStock(id, request)));
    }
}
