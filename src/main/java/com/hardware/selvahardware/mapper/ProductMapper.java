package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.product.ProductDto;
import com.hardware.selvahardware.dto.product.ProductRequest;
import com.hardware.selvahardware.entity.Product;

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
                .price(product.getPrice())
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
                .id(id)
                .name(req.getName())
                .code(req.getCode())
                .barcode(req.getBarcode())
                .price(req.getPrice())
                .stock(req.getStock())
                .unit(req.getUnit())
                .gstPercentage(req.getGstPercentage())
                .category(req.getCategory())
                .description(req.getDescription())
                .pricingType(req.getPricingType())
                .build();
    }

    public void updateEntity(Product product, ProductRequest req) {
        product.setName(req.getName());
        product.setCode(req.getCode());
        product.setBarcode(req.getBarcode());
        product.setPrice(req.getPrice());
        product.setStock(req.getStock());
        product.setUnit(req.getUnit());
        product.setGstPercentage(req.getGstPercentage());
        product.setCategory(req.getCategory());
        product.setDescription(req.getDescription());
        product.setPricingType(req.getPricingType());
    }
}
