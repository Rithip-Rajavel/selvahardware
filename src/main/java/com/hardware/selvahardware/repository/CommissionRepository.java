package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, UUID> {
    List<Commission> findByContractorIdOrderByCreatedAtDesc(UUID contractorId);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Commission c WHERE c.contractorId = :contractorId")
    BigDecimal sumByContractorId(@Param("contractorId") UUID contractorId);
}
