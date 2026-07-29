package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.InvoiceReturn;
import com.hardware.selvahardware.enums.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface InvoiceReturnRepository extends JpaRepository<InvoiceReturn, UUID> {

    List<InvoiceReturn> findByInvoiceId(UUID invoiceId);

    List<InvoiceReturn> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<InvoiceReturn> findByStatus(ReturnStatus status);

    @Query("SELECT ir FROM InvoiceReturn ir WHERE ir.customer.id = :customerId AND ir.status = :status ORDER BY ir.createdAt DESC")
    List<InvoiceReturn> findByCustomerIdAndStatus(@Param("customerId") UUID customerId, @Param("status") ReturnStatus status);

    @Query("SELECT ir FROM InvoiceReturn ir WHERE ir.createdAt >= :from AND ir.createdAt <= :to ORDER BY ir.createdAt DESC")
    List<InvoiceReturn> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(ir.totalReturnAmount), 0) FROM InvoiceReturn ir WHERE ir.status = 'processed' OR ir.status = 'refunded'")
    java.math.BigDecimal sumTotalReturnedAmount();

    @Query("SELECT COALESCE(SUM(ir.totalReturnAmount), 0) FROM InvoiceReturn ir WHERE ir.customer.id = :customerId AND (ir.status = 'processed' OR ir.status = 'refunded')")
    java.math.BigDecimal sumTotalReturnedAmountByCustomer(@Param("customerId") UUID customerId);
}
