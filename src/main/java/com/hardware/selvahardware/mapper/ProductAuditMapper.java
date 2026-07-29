package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.product.ProductAuditDto;
import com.hardware.selvahardware.entity.ProductAudit;
import org.springframework.stereotype.Component;

@Component
public class ProductAuditMapper {

    public ProductAuditDto toDto(ProductAudit audit) {
        if (audit == null) {
            return null;
        }
        return ProductAuditDto.builder()
                .id(audit.getId())
                .productId(audit.getProduct() != null ? audit.getProduct().getId() : null)
                .productName(audit.getProduct() != null ? audit.getProduct().getName() : null)
                .productCode(audit.getProduct() != null ? audit.getProduct().getCode() : null)
                .action(audit.getAction())
                .oldStock(audit.getOldStock())
                .newStock(audit.getNewStock())
                .stockChange(audit.getStockChange())
                .oldPrice(audit.getOldPrice())
                .newPrice(audit.getNewPrice())
                .priceChange(audit.getPriceChange())
                .cashAmount(audit.getCashAmount())
                .performedBy(audit.getPerformedBy())
                .performedByName(audit.getPerformedByName())
                .notes(audit.getNotes())
                .createdAt(audit.getCreatedAt())
                .build();
    }
}
