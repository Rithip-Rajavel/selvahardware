package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.ProductAudit;
import com.hardware.selvahardware.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductAuditRepository extends JpaRepository<ProductAudit, UUID> {
    @Query("SELECT pa FROM ProductAudit pa LEFT JOIN FETCH pa.product p WHERE p.id = :productId ORDER BY pa.createdAt DESC")
    List<ProductAudit> findByProductIdOrderByCreatedAtDesc(UUID productId);

    @Query("SELECT pa FROM ProductAudit pa LEFT JOIN FETCH pa.product p WHERE p.id = :productId AND pa.action = :action ORDER BY pa.createdAt DESC")
    List<ProductAudit> findByProductIdAndActionOrderByCreatedAtDesc(UUID productId, AuditAction action);
}
