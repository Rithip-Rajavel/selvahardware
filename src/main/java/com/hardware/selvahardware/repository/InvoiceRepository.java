package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.enums.BillType;
import com.hardware.selvahardware.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomerIdOrderByCreatedAtAsc(UUID customerId);

    @Query("SELECT i FROM Invoice i WHERE i.customer.id = :customerId AND i.status = 'pending' ORDER BY i.createdAt ASC")
    List<Invoice> findPendingInvoicesByCustomer(@Param("customerId") String customerId);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.items LEFT JOIN FETCH i.customer ORDER BY i.createdAt DESC")
    List<Invoice> findAllInvoicesWithItems();

    @Query(value = "SELECT * FROM invoices i WHERE " +
            "(:customerId IS NULL OR i.customer_id = :customerId) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:billType IS NULL OR i.bill_type = :billType) AND " +
            "(:fromDate IS NULL OR i.created_at >= :fromDate) AND " +
            "(:toDate IS NULL OR i.created_at <= :toDate) AND " +
            "(:search IS NULL OR i.invoice_number LIKE CONCAT('%', :search, '%') " +
            "OR i.customer_name LIKE CONCAT('%', :search, '%'))", nativeQuery = true)
    List<Invoice> filterInvoices(@Param("customerId") UUID customerId,
            @Param("status") InvoiceStatus status,
            @Param("billType") BillType billType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("search") String search);

    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.invoiceNumber LIKE CONCAT(:prefix, '%')")
    Optional<String> findLastInvoiceNumber(@Param("prefix") String prefix);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.createdAt >= :from AND i.createdAt <= :to")
    long countByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM Invoice i WHERE i.createdAt >= :from AND i.createdAt <= :to AND i.status != 'cancelled'")
    BigDecimal sumRevenueByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(i.balance), 0) FROM Invoice i WHERE i.status = 'pending'")
    BigDecimal sumPendingBalance();
}
