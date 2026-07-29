package com.hardware.selvahardware.enums;

/**
 * Types of audit actions for invoice audit log
 */
public enum AuditActionType {
    INVOICE_CREATED,           // When invoice is first created
    PAYMENT_RECEIVED,          // Partial or full payment received
    PAYMENT_DUE,               // Due date reached, payment not complete
    OVERDUE_PAYMENT,           // Overdue payment recorded
    STATUS_CHANGED,            // Invoice status change (pending -> paid, etc.)
    ITEM_RETURNED,             // Items returned from invoice
    BILL_WRITTEN_OFF,          // Bill partially or fully written off
    DISCOUNT_APPLIED,          // Additional discount applied
    INVOICE_CANCELLED,         // Invoice cancelled
    INVOICE_MODIFIED,          // Invoice details modified
    OVERDUE_REMINDER_SENT,     // Overdue reminder notification sent
    COMMISSION_PAID            // Commission paid to contractor
}
