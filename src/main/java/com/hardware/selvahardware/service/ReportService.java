package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.customer.CustomerDto;
import com.hardware.selvahardware.dto.invoice.InvoiceDto;
import com.hardware.selvahardware.dto.payment.PaymentDto;
import com.hardware.selvahardware.dto.commission.CommissionDto;
import com.hardware.selvahardware.dto.customer.CustomerBalanceDto;
import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.entity.Product;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.CommissionMapper;
import com.hardware.selvahardware.mapper.CustomerMapper;
import com.hardware.selvahardware.mapper.InvoiceMapper;
import com.hardware.selvahardware.mapper.PaymentMapper;
import com.hardware.selvahardware.repository.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CommissionRepository commissionRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceMapper invoiceMapper;
    private final CustomerMapper customerMapper;
    private final CommissionMapper commissionMapper;
    private final PaymentMapper paymentMapper;
    private final CustomerService customerService;

    public SalesReport getSalesReport(String period, String customerId, String category) {
        LocalDateTime[] range = getPeriodRange(period);
        List<Invoice> invoices = invoiceRepository.filterInvoices(customerId, null, null, range[0], range[1], null);

        BigDecimal totalRevenue = invoices.stream()
                .map(Invoice::getGrandTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = invoices.stream()
                .map(Invoice::getTotalDiscount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // By category
        Map<String, BigDecimal> byCategory = invoices.stream()
                .flatMap(inv -> inv.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> {
                            Product p = productRepository.findById(item.getProductId()).orElse(null);
                            return p != null && p.getCategory() != null ? p.getCategory() : "Other";
                        },
                        Collectors.reducing(BigDecimal.ZERO, item -> item.getTotal(), BigDecimal::add)
                ));

        List<CategoryStat> categoryStat = byCategory.entrySet().stream()
                .map(e -> new CategoryStat(e.getKey(), e.getValue(), 0))
                .collect(Collectors.toList());

        // By customer
        Map<String, Long> customerInvoiceCount = invoices.stream()
                .collect(Collectors.groupingBy(Invoice::getCustomerName, Collectors.counting()));
        Map<String, BigDecimal> customerRevenue = invoices.stream()
                .collect(Collectors.groupingBy(Invoice::getCustomerName,
                        Collectors.reducing(BigDecimal.ZERO, Invoice::getGrandTotal, BigDecimal::add)));

        List<CustomerStat> customerStat = customerRevenue.entrySet().stream()
                .map(e -> new CustomerStat(e.getKey(), e.getValue(), customerInvoiceCount.getOrDefault(e.getKey(), 0L)))
                .collect(Collectors.toList());

        return SalesReport.builder()
                .summary(SalesSummary.builder()
                        .totalRevenue(totalRevenue)
                        .totalInvoices(invoices.size())
                        .averageInvoiceValue(invoices.isEmpty() ? BigDecimal.ZERO : totalRevenue.divide(BigDecimal.valueOf(invoices.size()), 2, java.math.RoundingMode.HALF_UP))
                        .totalDiscount(totalDiscount)
                        .build())
                .byCategory(categoryStat)
                .byCustomer(customerStat)
                .build();
    }

    public LedgerReport getLedger(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", customerId);
        }
        CustomerDto customer = customerMapper.toDto(customerRepository.findById(customerId).get());
        CustomerBalanceDto balance = customerService.getCustomerBalance(customerId);
        List<InvoiceDto> invoices = invoiceRepository.findByCustomerIdOrderByCreatedAtAsc(customerId)
                .stream().map(invoiceMapper::toDto).collect(Collectors.toList());
        List<PaymentDto> transactions = paymentRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(paymentMapper::toDto).collect(Collectors.toList());
        List<CommissionDto> commissions = commissionRepository.findByContractorIdOrderByCreatedAtDesc(customerId)
                .stream().map(commissionMapper::toDto).collect(Collectors.toList());

        return LedgerReport.builder()
                .customer(customer)
                .balance(balance)
                .invoices(invoices)
                .transactions(transactions)
                .commissions(commissions)
                .build();
    }

    public InventoryReport getInventoryReport(boolean lowStock, String category) {
        List<Product> products = lowStock
                ? productRepository.findLowStockProducts()
                : productRepository.searchProducts(category, null);

        BigDecimal totalValue = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long lowStockCount = products.stream().filter(p -> p.getStock() <= 5).count();

        return InventoryReport.builder()
                .summary(InventorySummary.builder()
                        .totalProducts(products.size())
                        .totalStockValue(totalValue)
                        .lowStockCount(lowStockCount)
                        .build())
                .products(products.stream().map(p -> ProductStat.builder()
                        .id(p.getId()).name(p.getName()).code(p.getCode())
                        .stock(p.getStock()).price(p.getPrice()).category(p.getCategory()).build())
                        .collect(Collectors.toList()))
                .build();
    }

    public CommissionReport getCommissionReport(String contractorId, String fromDate, String toDate) {
        LocalDateTime from = fromDate != null ? LocalDateTime.parse(fromDate + "T00:00:00") : null;
        LocalDateTime to = toDate != null ? LocalDateTime.parse(toDate + "T23:59:59") : null;
        var commissions = commissionRepository.filterCommissions(contractorId, from, to);
        BigDecimal total = commissions.stream().map(c -> c.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        long contractorCount = commissions.stream().map(c -> c.getContractorId()).distinct().count();
        return CommissionReport.builder()
                .summary(CommissionSummary.builder().totalCommission(total).contractorCount(contractorCount).build())
                .byContractor(commissions.stream().map(commissionMapper::toDto).collect(Collectors.toList()))
                .build();
    }

    private LocalDateTime[] getPeriodRange(String period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period != null ? period : "month") {
            case "today" -> new LocalDateTime[]{now.toLocalDate().atStartOfDay(), now.toLocalDate().atTime(23, 59, 59)};
            case "week" -> new LocalDateTime[]{now.minusDays(7), now};
            case "year" -> new LocalDateTime[]{now.withDayOfYear(1).toLocalDate().atStartOfDay(), now};
            default -> new LocalDateTime[]{now.withDayOfMonth(1).toLocalDate().atStartOfDay(), now};
        };
    }

    // Inner report/stat classes
    @Data @Builder public static class SalesReport { private SalesSummary summary; private List<CategoryStat> byCategory; private List<CustomerStat> byCustomer; }
    @Data @Builder public static class SalesSummary { private BigDecimal totalRevenue; private int totalInvoices; private BigDecimal averageInvoiceValue; private BigDecimal totalDiscount; }
    @Data @lombok.AllArgsConstructor public static class CategoryStat { private String category; private BigDecimal revenue; private long quantity; }
    @Data @lombok.AllArgsConstructor public static class CustomerStat { private String customerName; private BigDecimal revenue; private long invoiceCount; }
    @Data @Builder public static class LedgerReport { private CustomerDto customer; private CustomerBalanceDto balance; private List<InvoiceDto> invoices; private List<PaymentDto> transactions; private List<CommissionDto> commissions; }
    @Data @Builder public static class InventoryReport { private InventorySummary summary; private List<ProductStat> products; }
    @Data @Builder public static class InventorySummary { private long totalProducts; private BigDecimal totalStockValue; private long lowStockCount; }
    @Data @Builder public static class ProductStat { private String id; private String name; private String code; private int stock; private BigDecimal price; private String category; }
    @Data @Builder public static class CommissionReport { private CommissionSummary summary; private List<CommissionDto> byContractor; }
    @Data @Builder public static class CommissionSummary { private BigDecimal totalCommission; private long contractorCount; }
}
