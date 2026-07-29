package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.EWayBill;
import com.hardware.selvahardware.enums.EWayBillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EWayBillRepository extends JpaRepository<EWayBill, UUID> {

    Optional<EWayBill> findByEwayBillNo(String ewayBillNo);

    List<EWayBill> findByInvoiceId(UUID invoiceId);

    List<EWayBill> findByStatus(EWayBillStatus status);

    List<EWayBill> findByGeneratedBy(String generatedBy);

    @Query("SELECT e FROM EWayBill e WHERE e.generatedDate >= :startDate AND e.generatedDate <= :endDate")
    List<EWayBill> findByGeneratedDateBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                              @Param("endDate") java.time.LocalDateTime endDate);

    boolean existsByEwayBillNo(String ewayBillNo);

    @Query("SELECT COUNT(e) FROM EWayBill e WHERE e.status = :status")
    long countByStatus(@Param("status") EWayBillStatus status);
}
