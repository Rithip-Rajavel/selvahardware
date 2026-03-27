package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.product.ProductDto;
import com.hardware.selvahardware.dto.product.ProductRequest;
import com.hardware.selvahardware.dto.product.StockUpdateRequest;
import com.hardware.selvahardware.entity.Product;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.ProductMapper;
import com.hardware.selvahardware.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDto> getAllProducts(String category, String search, boolean lowStock) {
        if (lowStock) {
            return productRepository.findLowStockProducts().stream()
                    .map(productMapper::toDto).collect(Collectors.toList());
        }
        return productRepository.searchProducts(category, search).stream()
                .map(productMapper::toDto).collect(Collectors.toList());
    }

    public ProductDto getProductById(UUID id) {
        return productMapper.toDto(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id)));
    }

    public ProductDto createProduct(ProductRequest request) {
        if (productRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Product code already exists: " + request.getCode());
        }
        Product product = productMapper.toEntity(request, null);
        return productMapper.toDto(productRepository.save(product));
    }

    public ProductDto updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productMapper.updateEntity(product, request);
        return productMapper.toDto(productRepository.save(product));
    }

    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
    }

    public ProductDto updateStock(UUID id, StockUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        int newStock;
        if ("subtract".equals(request.getOperation())) {
            newStock = product.getStock() - request.getQuantity();
            if (newStock < 0) {
                throw new BusinessException("Insufficient stock. Available: " + product.getStock());
            }
        } else {
            newStock = product.getStock() + request.getQuantity();
        }
        product.setStock(newStock);
        return productMapper.toDto(productRepository.save(product));
    }

    public ProductDto searchByCodeOrBarcode(String q) {
        return productMapper.toDto(productRepository.findByCodeOrBarcode(q)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with code/barcode: " + q)));
    }
}
