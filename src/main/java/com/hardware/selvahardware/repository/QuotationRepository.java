package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Quotation;
import com.hardware.selvahardware.enums.QuotationStatus;
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
public interface QuotationRepository extends JpaRepository<Quotation, UUID> {
    Optional<Quotation> findByQuotationNumber(String quotationNumber);

    boolean existsByQuotationNumber(String quotationNumber);

    List<Quotation> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<Quotation> findByStatusOrderByCreatedAtDesc(QuotationStatus status);

    @Query("SELECT q FROM Quotation q WHERE q.customer.id = :customerId AND q.status = :status ORDER BY q.createdAt DESC")
    List<Quotation> findByCustomerAndStatus(@Param("customerId") UUID customerId, @Param("status") QuotationStatus status);

    @Query("SELECT q FROM Quotation q LEFT JOIN FETCH q.items LEFT JOIN FETCH q.customer ORDER BY q.createdAt DESC")
    List<Quotation> findAllQuotationsWithItems();

    @Query("SELECT q FROM Quotation q LEFT JOIN FETCH q.items LEFT JOIN FETCH q.customer WHERE q.id = :id")
    Optional<Quotation> findByIdWithItems(@Param("id") UUID id);

    @Query(value = "SELECT * FROM quotations q WHERE " +
            "(:customerId IS NULL OR q.customer_id = :customerId) AND " +
            "(:status IS NULL OR q.status = :status) AND " +
            "(:fromDate IS NULL OR q.created_at >= :fromDate) AND " +
            "(:toDate IS NULL OR q.created_at <= :toDate) AND " +
            "(:search IS NULL OR q.quotation_number LIKE CONCAT('%', :search, '%') " +
            "OR q.customer_name LIKE CONCAT('%', :search, '%'))", nativeQuery = true)
    List<Quotation> filterQuotations(@Param("customerId") UUID customerId,
            @Param("status") QuotationStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("search") String search);

    @Query("SELECT MAX(q.quotationNumber) FROM Quotation q WHERE q.quotationNumber LIKE CONCAT(:prefix, '%')")
    Optional<String> findLastQuotationNumber(@Param("prefix") String prefix);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.createdAt >= :from AND q.createdAt <= :to")
    long countByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(q.grandTotal), 0) FROM Quotation q WHERE q.createdAt >= :from AND q.createdAt <= :to AND q.status != 'cancelled'")
    BigDecimal sumQuotationValueByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT q FROM Quotation q WHERE q.validUntil < :currentDateTime AND q.status = 'pending'")
    List<Quotation> findExpiredQuotations(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT q FROM Quotation q WHERE q.convertedToInvoice IS NOT NULL")
    List<Quotation> findConvertedQuotations();
}
