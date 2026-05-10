package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.GstBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GstBillRepository extends JpaRepository<GstBill, UUID> {

    Optional<GstBill> findByInvoiceId(UUID invoiceId);

    List<GstBill> findByInvoiceIdIn(List<UUID> invoiceIds);

    List<GstBill> findByCustomerNameContainingIgnoreCase(String customerName);

    List<GstBill> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);

    List<GstBill> findByCustomerNameContainingIgnoreCaseAndInvoiceDateBetween(
            String customerName, LocalDate startDate, LocalDate endDate);

    @Query("SELECT g FROM GstBill g WHERE g.invoiceNumber = :invoiceNumber")
    Optional<GstBill> findByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);

    @Query("SELECT COUNT(g) FROM GstBill g WHERE g.invoiceDate >= :startDate AND g.invoiceDate <= :endDate")
    long countByInvoiceDateBetween(@Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT g FROM GstBill g WHERE " +
            "(:customerName IS NULL OR LOWER(g.customerName) LIKE LOWER(CONCAT('%', :customerName, '%'))) AND " +
            "(:fromDate IS NULL OR g.invoiceDate >= :fromDate) AND " +
            "(:toDate IS NULL OR g.invoiceDate <= :toDate)")
    List<GstBill> findWithFilters(@Param("customerName") String customerName,
                                   @Param("fromDate") LocalDate fromDate,
                                   @Param("toDate") LocalDate toDate);

    boolean existsByInvoiceNumber(String invoiceNumber);

    void deleteByInvoiceId(UUID invoiceId);
}
