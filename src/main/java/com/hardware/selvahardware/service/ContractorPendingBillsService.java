package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.contractor.ContractorPendingBillsDto;
import com.hardware.selvahardware.dto.contractor.ContractorPendingSummaryDto;
import com.hardware.selvahardware.dto.contractor.CustomerPendingSummaryDto;
import com.hardware.selvahardware.dto.invoice.InvoiceDto;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.enums.CustomerType;
import com.hardware.selvahardware.enums.InvoiceStatus;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.InvoiceMapper;
import com.hardware.selvahardware.repository.CustomerRepository;
import com.hardware.selvahardware.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractorPendingBillsService {

        private final CustomerRepository customerRepository;
        private final InvoiceRepository invoiceRepository;
        private final InvoiceMapper invoiceMapper;

        /**
         * Get complete pending bills information for a contractor
         * including all linked customers and their pending bills
         */
        @Transactional(readOnly = true)
        public ContractorPendingBillsDto getContractorPendingBills(UUID contractorId) {
                Customer contractor = customerRepository.findById(contractorId)
                                .orElseThrow(() -> new ResourceNotFoundException("Contractor", contractorId));

                if (contractor.getType() != CustomerType.contractor) {
                        throw new RuntimeException("Customer is not a contractor: " + contractorId);
                }

                // Get all customers linked to this contractor
                List<Customer> linkedCustomers = customerRepository.findByContractorId(contractorId);

                // Build customer pending summaries
                List<CustomerPendingSummaryDto> customerSummaries = new ArrayList<>();
                BigDecimal grandTotalBilled = BigDecimal.ZERO;
                BigDecimal grandTotalPaid = BigDecimal.ZERO;
                BigDecimal grandTotalPending = BigDecimal.ZERO;
                long totalPendingInvoiceCount = 0;
                long customersWithPendingBills = 0;

                for (Customer customer : linkedCustomers) {
                        List<Invoice> pendingInvoices = invoiceRepository
                                        .findByCustomerIdOrderByCreatedAtAsc(customer.getId())
                                        .stream()
                                        .filter(inv -> inv.getStatus() == InvoiceStatus.pending)
                                        .collect(Collectors.toList());

                        // Initialize lazy-loaded collections
                        for (Invoice invoice : pendingInvoices) {
                                Hibernate.initialize(invoice.getItems());
                        }

                        if (!pendingInvoices.isEmpty()) {
                                customersWithPendingBills++;
                        }

                        List<InvoiceDto> pendingInvoiceDtos = pendingInvoices.stream()
                                        .map(invoiceMapper::toDto)
                                        .collect(Collectors.toList());

                        BigDecimal totalBilled = pendingInvoices.stream()
                                        .map(Invoice::getGrandTotal)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal totalPaid = pendingInvoices.stream()
                                        .map(Invoice::getAmountPaid)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal totalPending = pendingInvoices.stream()
                                        .map(Invoice::getBalance)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        grandTotalBilled = grandTotalBilled.add(totalBilled);
                        grandTotalPaid = grandTotalPaid.add(totalPaid);
                        grandTotalPending = grandTotalPending.add(totalPending);
                        totalPendingInvoiceCount += pendingInvoices.size();

                        CustomerPendingSummaryDto summary = CustomerPendingSummaryDto.builder()
                                        .customerId(customer.getId())
                                        .customerName(customer.getName())
                                        .customerPhone(customer.getPhone())
                                        .customerEmail(customer.getEmail())
                                        .customerAddress(customer.getAddress())
                                        .customerCity(customer.getCity())
                                        .pendingInvoices(pendingInvoiceDtos)
                                        .totalBilled(totalBilled)
                                        .totalPaid(totalPaid)
                                        .totalPending(totalPending)
                                        .pendingInvoiceCount(pendingInvoices.size())
                                        .build();

                        customerSummaries.add(summary);
                }

                // Get contractor's own pending bills (if contractor has invoices as a customer)
                List<Invoice> contractorPendingInvoices = invoiceRepository
                                .findByCustomerIdOrderByCreatedAtAsc(contractorId)
                                .stream()
                                .filter(inv -> inv.getStatus() == InvoiceStatus.pending)
                                .collect(Collectors.toList());

                // Initialize lazy-loaded collections for contractor's own invoices
                for (Invoice invoice : contractorPendingInvoices) {
                        Hibernate.initialize(invoice.getItems());
                }

                ContractorPendingSummaryDto ownPendingBills = null;
                if (!contractorPendingInvoices.isEmpty()) {
                        List<InvoiceDto> ownPendingInvoiceDtos = contractorPendingInvoices.stream()
                                        .map(invoiceMapper::toDto)
                                        .collect(Collectors.toList());

                        BigDecimal ownTotalBilled = contractorPendingInvoices.stream()
                                        .map(Invoice::getGrandTotal)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal ownTotalPaid = contractorPendingInvoices.stream()
                                        .map(Invoice::getAmountPaid)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal ownTotalPending = contractorPendingInvoices.stream()
                                        .map(Invoice::getBalance)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        grandTotalBilled = grandTotalBilled.add(ownTotalBilled);
                        grandTotalPaid = grandTotalPaid.add(ownTotalPaid);
                        grandTotalPending = grandTotalPending.add(ownTotalPending);
                        totalPendingInvoiceCount += contractorPendingInvoices.size();

                        ownPendingBills = ContractorPendingSummaryDto.builder()
                                        .pendingInvoices(ownPendingInvoiceDtos)
                                        .totalBilled(ownTotalBilled)
                                        .totalPaid(ownTotalPaid)
                                        .totalPending(ownTotalPending)
                                        .pendingInvoiceCount(contractorPendingInvoices.size())
                                        .build();
                }

                return ContractorPendingBillsDto.builder()
                                .contractorId(contractor.getId())
                                .contractorName(contractor.getName())
                                .contractorPhone(contractor.getPhone())
                                .contractorEmail(contractor.getEmail())
                                .contractorAddress(contractor.getAddress())
                                .contractorCity(contractor.getCity())
                                .isActive(contractor.getIsActive())
                                .lastPaymentDate(contractor.getLastPaymentDate())
                                .lastInvoiceDate(contractor.getLastInvoiceDate())
                                .linkedCustomers(customerSummaries)
                                .ownPendingBills(ownPendingBills)
                                .grandTotalBilled(grandTotalBilled)
                                .grandTotalPaid(grandTotalPaid)
                                .grandTotalPending(grandTotalPending)
                                .totalPendingInvoiceCount(totalPendingInvoiceCount)
                                .totalLinkedCustomers(linkedCustomers.size())
                                .customersWithPendingBills(customersWithPendingBills)
                                .build();
        }

        /**
         * Get pending bills summary for all contractors
         */
        @Transactional(readOnly = true)
        public List<ContractorPendingBillsDto> getAllContractorsPendingBills() {
                List<Customer> contractors = customerRepository.findByType(CustomerType.contractor);
                return contractors.stream()
                                .map(c -> getContractorPendingBills(c.getId()))
                                .collect(Collectors.toList());
        }
}
