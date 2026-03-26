package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, String> {
    List<Commission> findByContractorIdOrderByCreatedAtDesc(String contractorId);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Commission c WHERE c.contractorId = :contractorId")
    BigDecimal sumByContractorId(@Param("contractorId") String contractorId);

    @Query("SELECT c FROM Commission c WHERE " +
           "(:contractorId IS NULL OR c.contractorId = :contractorId) AND " +
           "(:fromDate IS NULL OR c.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR c.createdAt <= :toDate)")
    List<Commission> filterCommissions(@Param("contractorId") String contractorId,
                                        @Param("fromDate") LocalDateTime fromDate,
                                        @Param("toDate") LocalDateTime toDate);
}
