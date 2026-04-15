package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.product.ProductDto;
import com.hardware.selvahardware.dto.product.ProductRequest;
import com.hardware.selvahardware.dto.product.StockUpdateRequest;
import com.hardware.selvahardware.entity.Product;
import com.hardware.selvahardware.entity.LastPaymentValue;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.ProductMapper;
import com.hardware.selvahardware.repository.ProductRepository;
import com.hardware.selvahardware.repository.LastPaymentValueRepository;
import com.hardware.selvahardware.repository.PriceMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final LastPaymentValueRepository lastPaymentValueRepository;
    private final PriceMappingRepository priceMappingRepository;
    private final ProductAuditService productAuditService;

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
        Product savedProduct = productRepository.save(product);

        // Log product creation
        productAuditService.logProductCreation(savedProduct, null, "System");

        return productMapper.toDto(savedProduct);
    }

    public ProductDto updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productMapper.updateEntity(product, request);
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }

        // Clean up related data
        // 1. Delete all references in last_payment_values
        List<LastPaymentValue> references = lastPaymentValueRepository.findLastPaymentValuesByProduct(id);
        if (!references.isEmpty()) {
            for (LastPaymentValue ref : references) {
                lastPaymentValueRepository.delete(ref);
            }
        }

        // 2. Delete all references in price_mapping
        priceMappingRepository.deleteByProductId(id);

        // 3. Finally delete the product
        productRepository.deleteById(id);
    }

    public ProductDto updateStock(UUID id, StockUpdateRequest request) {
        return updateStock(id, request, null, "System", null);
    }

    @Transactional
    public ProductDto updateStock(UUID id, StockUpdateRequest request, java.math.BigDecimal cashAmount,
            String performedByName, String notes) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        int newStock;
        if ("subtract".equals(request.getOperation())) {
            newStock = product.getStock() - request.getQuantity();
            if (newStock < 0) {
                throw new BusinessException("Insufficient stock. Available: " + product.getStock());
            }
            productAuditService.logStockDecrease(product, request.getQuantity(), null, performedByName, cashAmount,
                    notes);
        } else {
            newStock = product.getStock() + request.getQuantity();
            productAuditService.logStockIncrease(product, request.getQuantity(), null, performedByName, notes);
        }
        product.setStock(newStock);
        return productMapper.toDto(productRepository.save(product));
    }

    public ProductDto searchByCodeOrBarcode(String q) {
        return productMapper.toDto(productRepository.findByCodeOrBarcode(q)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with code/barcode: " + q)));
    }

    @Transactional
    public ProductDto updatePrice(UUID id, com.hardware.selvahardware.dto.product.PriceUpdateRequest request) {
        return updatePrice(id, request, null, "System");
    }

    @Transactional
    public ProductDto updatePrice(UUID id, com.hardware.selvahardware.dto.product.PriceUpdateRequest request,
            UUID performedBy, String performedByName) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        java.math.BigDecimal oldPrice = product.getPrice();
        java.math.BigDecimal newPrice = request.getNewPrice();

        productAuditService.logPriceUpdate(product, oldPrice, newPrice, performedBy, performedByName,
                request.getNotes());

        return productMapper.toDto(product);
    }
}
