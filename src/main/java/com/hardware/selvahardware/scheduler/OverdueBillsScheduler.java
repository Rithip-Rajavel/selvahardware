package com.hardware.selvahardware.scheduler;

import com.hardware.selvahardware.service.OverdueBillsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueBillsScheduler {

    private final OverdueBillsService overdueBillsService;

    /**
     * Runs daily at 2:00 AM to check and mark overdue invoices.
     * Logs overdue events for invoices past their due date.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkOverdueBills() {
        log.info("Running scheduled overdue bills check...");
        overdueBillsService.checkAllOverdueInvoices();
        log.info("Overdue bills check completed");
    }
}
