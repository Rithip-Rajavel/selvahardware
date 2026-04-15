package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.product.ProductAuditDto;
import com.hardware.selvahardware.entity.Product;
import com.hardware.selvahardware.entity.ProductAudit;
import com.hardware.selvahardware.enums.AuditAction;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.ProductAuditMapper;
import com.hardware.selvahardware.repository.ProductAuditRepository;
import com.hardware.selvahardware.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAuditService {

    private final ProductAuditRepository auditRepository;
    private final ProductRepository productRepository;
    private final ProductAuditMapper auditMapper;

    public List<ProductAuditDto> getAuditsByProductId(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }
        return auditRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(auditMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductAuditDto> getAuditsByProductIdAndAction(UUID productId, AuditAction action) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }
        return auditRepository.findByProductIdAndActionOrderByCreatedAtDesc(productId, action).stream()
                .map(auditMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductAudit logProductCreation(Product product, UUID performedBy, String performedByName) {
        ProductAudit audit = ProductAudit.builder()
                .product(product)
                .action(AuditAction.PRODUCT_CREATED)
                .oldStock(0)
                .newStock(product.getStock())
                .stockChange(product.getStock())
                .oldPrice(BigDecimal.ZERO)
                .newPrice(product.getPrice())
                .priceChange(product.getPrice())
                .performedBy(performedBy)
                .performedByName(performedByName)
                .notes("Product created")
                .build();
        return auditRepository.save(audit);
    }

    @Transactional
    public ProductAudit logStockIncrease(Product product, Integer stockAdded, UUID performedBy, String performedByName, String notes) {
        Integer oldStock = product.getStock();
        Integer newStock = oldStock + stockAdded;
        product.setStock(newStock);
        productRepository.save(product);

        ProductAudit audit = ProductAudit.builder()
                .product(product)
                .action(AuditAction.STOCK_INCREASED)
                .oldStock(oldStock)
                .newStock(newStock)
                .stockChange(stockAdded)
                .performedBy(performedBy)
                .performedByName(performedByName)
                .notes(notes != null ? notes : "Stock increased")
                .build();
        return auditRepository.save(audit);
    }

    @Transactional
    public ProductAudit logStockDecrease(Product product, Integer stockRemoved, UUID performedBy, String performedByName, BigDecimal cashAmount, String notes) {
        Integer oldStock = product.getStock();
        Integer newStock = Math.max(0, oldStock - stockRemoved);
        product.setStock(newStock);
        productRepository.save(product);

        ProductAudit audit = ProductAudit.builder()
                .product(product)
                .action(AuditAction.STOCK_DECREASED)
                .oldStock(oldStock)
                .newStock(newStock)
                .stockChange(-stockRemoved)
                .cashAmount(cashAmount)
                .performedBy(performedBy)
                .performedByName(performedByName)
                .notes(notes != null ? notes : "Stock decreased")
                .build();
        return auditRepository.save(audit);
    }

    @Transactional
    public ProductAudit logPriceUpdate(Product product, BigDecimal oldPrice, BigDecimal newPrice, UUID performedBy, String performedByName, String notes) {
        product.setPrice(newPrice);
        productRepository.save(product);

        BigDecimal priceChange = newPrice.subtract(oldPrice);

        ProductAudit audit = ProductAudit.builder()
                .product(product)
                .action(AuditAction.PRICE_UPDATED)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .priceChange(priceChange)
                .performedBy(performedBy)
                .performedByName(performedByName)
                .notes(notes != null ? notes : "Price updated")
                .build();
        return auditRepository.save(audit);
    }

    @Transactional
    public ProductAudit logStockAndPriceUpdate(Product product, Integer oldStock, Integer newStock, Integer stockChange, 
                                                BigDecimal oldPrice, BigDecimal newPrice, BigDecimal priceChange,
                                                UUID performedBy, String performedByName, String notes) {
        product.setStock(newStock);
        product.setPrice(newPrice);
        productRepository.save(product);

        ProductAudit audit = ProductAudit.builder()
                .product(product)
                .action(AuditAction.STOCK_AND_PRICE_UPDATED)
                .oldStock(oldStock)
                .newStock(newStock)
                .stockChange(stockChange)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .priceChange(priceChange)
                .performedBy(performedBy)
                .performedByName(performedByName)
                .notes(notes != null ? notes : "Stock and price updated")
                .build();
        return auditRepository.save(audit);
    }
}
