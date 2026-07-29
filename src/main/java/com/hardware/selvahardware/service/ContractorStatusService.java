package com.hardware.selvahardware.service;

import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.enums.CustomerType;
import com.hardware.selvahardware.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractorStatusService {

    private final CustomerRepository customerRepository;

    /**
     * Updates the isActive status for all contractors.
     * A contractor becomes inactive if:
     * - They have a last payment date
     * - No invoice has been created for 1 month after the last payment date
     */
    public void updateContractorStatus() {
        log.info("Starting contractor status update job...");

        List<Customer> contractors = customerRepository.findByType(CustomerType.contractor);
        LocalDateTime now = LocalDateTime.now();

        int activatedCount = 0;
        int deactivatedCount = 0;

        for (Customer contractor : contractors) {
            boolean shouldBeActive = determineIfActive(contractor, now);

            if (shouldBeActive != Boolean.TRUE.equals(contractor.getIsActive())) {
                contractor.setIsActive(shouldBeActive);
                customerRepository.save(contractor);

                if (shouldBeActive) {
                    activatedCount++;
                    log.info("Contractor {} activated", contractor.getName());
                } else {
                    deactivatedCount++;
                    log.info("Contractor {} deactivated (no bill for 1 month after last payment)",
                            contractor.getName());
                }
            }
        }

        log.info("Contractor status update completed. Activated: {}, Deactivated: {}", activatedCount,
                deactivatedCount);
    }

    /**
     * Determines if a contractor should be active based on payment and invoice
     * dates.
     * Rules:
     * - If no lastPaymentDate, contractor remains in current state (default true)
     * - If lastInvoiceDate is after lastPaymentDate, contractor is active
     * - If lastInvoiceDate is null or before lastPaymentDate, and more than 1 month
     * has passed since lastPaymentDate, contractor is inactive
     */
    private boolean determineIfActive(Customer contractor, LocalDateTime now) {
        LocalDateTime lastPaymentDate = contractor.getLastPaymentDate();
        LocalDateTime lastInvoiceDate = contractor.getLastInvoiceDate();

        // If no payment date recorded, default to active
        if (lastPaymentDate == null) {
            return true;
        }

        // If there's an invoice after the last payment, contractor is active
        if (lastInvoiceDate != null && lastInvoiceDate.isAfter(lastPaymentDate)) {
            return true;
        }

        // Check if 1 month has passed since last payment
        long monthsSincePayment = ChronoUnit.MONTHS.between(lastPaymentDate, now);

        // If less than 1 month since last payment, contractor is active
        if (monthsSincePayment < 1) {
            return true;
        }

        // If 1 month or more has passed since last payment and no invoice created after
        // payment, deactivate
        return false;
    }

    /**
     * Manually refresh status for a specific contractor
     */
    public void refreshContractorStatus(UUID contractorId) {
        Customer contractor = customerRepository.findById(contractorId)
                .orElseThrow(() -> new RuntimeException("Contractor not found: " + contractorId));

        if (contractor.getType() != CustomerType.contractor) {
            throw new RuntimeException("Customer is not a contractor: " + contractorId);
        }

        boolean shouldBeActive = determineIfActive(contractor, LocalDateTime.now());
        contractor.setIsActive(shouldBeActive);
        customerRepository.save(contractor);
    }
}
