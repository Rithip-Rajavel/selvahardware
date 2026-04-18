package com.hardware.selvahardware.enums;

/**
 * Status of invoice return/written bill
 */
public enum ReturnStatus {
    pending,      // Return request created, awaiting processing
    approved,     // Return approved
    processed,    // Return processed, stock updated
    refunded,     // Refund completed
    rejected,     // Return rejected
    cancelled     // Return cancelled
}
