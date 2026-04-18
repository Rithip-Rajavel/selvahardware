package com.hardware.selvahardware.scheduler;

import com.hardware.selvahardware.service.ContractorStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractorStatusScheduler {

    private final ContractorStatusService contractorStatusService;

    /**
     * Runs daily at 1:00 AM to update contractor active status.
     * A contractor becomes inactive if no bill is created for 1 month after the last payment.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateContractorStatus() {
        log.info("Running scheduled contractor status update...");
        contractorStatusService.updateContractorStatus();
    }
}
