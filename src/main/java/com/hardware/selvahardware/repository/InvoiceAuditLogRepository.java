package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.InvoiceAuditLog;
import com.hardware.selvahardware.enums.AuditActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface InvoiceAuditLogRepository extends JpaRepository<InvoiceAuditLog, UUID> {

    List<InvoiceAuditLog> findByInvoiceIdOrderByCreatedAtAsc(UUID invoiceId);

    List<InvoiceAuditLog> findByInvoiceIdOrderByCreatedAtDesc(UUID invoiceId);

    List<InvoiceAuditLog> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<InvoiceAuditLog> findByActionType(AuditActionType actionType);

    @Query("SELECT ial FROM InvoiceAuditLog ial WHERE ial.invoiceId = :invoiceId AND ial.actionType IN :actionTypes ORDER BY ial.createdAt ASC")
    List<InvoiceAuditLog> findByInvoiceIdAndActionTypes(@Param("invoiceId") UUID invoiceId, @Param("actionTypes") List<AuditActionType> actionTypes);

    @Query("SELECT ial FROM InvoiceAuditLog ial WHERE ial.customer.id = :customerId AND ial.actionType = 'OVERDUE_PAYMENT' ORDER BY ial.createdAt DESC")
    List<InvoiceAuditLog> findOverduePaymentsByCustomer(@Param("customerId") UUID customerId);

    @Query("SELECT ial FROM InvoiceAuditLog ial WHERE ial.actionType = 'OVERDUE_PAYMENT' ORDER BY ial.createdAt DESC")
    List<InvoiceAuditLog> findAllOverduePayments();
}
