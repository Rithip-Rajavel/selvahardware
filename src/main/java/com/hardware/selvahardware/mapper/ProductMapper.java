package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.product.ProductDto;
import com.hardware.selvahardware.dto.product.ProductRequest;
import com.hardware.selvahardware.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .code(product.getCode())
                .barcode(product.getBarcode())
                .price(product.getPrice()) // Selling price
                .originalAmount(product.getOriginalAmount()) // Cost price
                .stock(product.getStock())
                .unit(product.getUnit())
                .gstPercentage(product.getGstPercentage())
                .category(product.getCategory())
                .description(product.getDescription())
                .pricingType(product.getPricingType())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toEntity(ProductRequest req, UUID id) {
        return Product.builder()
                .name(req.getName())
                .code(req.getCode())
                .barcode(req.getBarcode())
                .price(req.getPrice() != null ? BigDecimal.valueOf(req.getPrice()) : null) // Selling price
                .originalAmount(req.getOriginalAmount() != null ? BigDecimal.valueOf(req.getOriginalAmount()) : null) // Cost price
                .stock(req.getStock())
                .unit(req.getUnit())
                .gstPercentage(req.getGstPercentage() != null ? BigDecimal.valueOf(req.getGstPercentage()) : null)
                .category(req.getCategory())
                .description(req.getDescription())
                .pricingType(req.getPricingType())
                .build();
    }

    public void updateEntity(Product product, ProductRequest req) {
        product.setName(req.getName());
        product.setCode(req.getCode());
        product.setBarcode(req.getBarcode());
        product.setPrice(req.getPrice() != null ? BigDecimal.valueOf(req.getPrice()) : null); // Selling price
        product.setOriginalAmount(req.getOriginalAmount() != null ? BigDecimal.valueOf(req.getOriginalAmount()) : null); // Cost price
        product.setStock(req.getStock());
        product.setUnit(req.getUnit());
        product.setGstPercentage(req.getGstPercentage() != null ? BigDecimal.valueOf(req.getGstPercentage()) : null);
        product.setCategory(req.getCategory());
        product.setDescription(req.getDescription());
        product.setPricingType(req.getPricingType());
    }
}
