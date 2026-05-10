package com.hardware.selvahardware.entity;

import com.hardware.selvahardware.enums.EWayBillStatus;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "eway_bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EWayBill {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Invoice invoice;

    @Column(name = "eway_bill_no", length = 12, unique = true)
    private String ewayBillNo;

    @Column(name = "generated_date", nullable = false)
    private LocalDateTime generatedDate;

    @Column(name = "generated_by", length = 50, nullable = false)
    private String generatedBy;

    @Column(name = "valid_upto")
    private LocalDate validUpto;

    @Column(length = 10)
    @Builder.Default
    private String portal = "1";

    // Transportation Details
    @Column(name = "transport_mode", length = 10)
    private String mode;

    @Column(name = "approx_distance")
    private Integer approxDistance;

    @Column(name = "supply_type", length = 20)
    private String type;

    @Column(name = "document_type", length = 20)
    private String documentType;

    @Column(name = "document_number", length = 50)
    private String documentNumber;

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "transaction_type", length = 20)
    private String transactionType;

    // FROM (Supplier) - Auto-filled from settings
    @Column(name = "from_gstin", length = 20)
    private String fromGstin;

    @Column(name = "from_name", length = 255)
    private String fromName;

    @Column(name = "from_address", columnDefinition = "TEXT")
    private String fromAddress;

    @Column(name = "from_place", length = 100)
    @Builder.Default
    private String fromPlace = "Jolarpet";

    @Column(name = "from_pincode", length = 6)
    @Builder.Default
    private String fromPincode = "635851";

    @Column(name = "from_state", length = 50)
    @Builder.Default
    private String fromState = "Tamil Nadu";

    // TO (Recipient) - User-entered
    @Column(name = "to_gstin", length = 20)
    private String toGstin;

    @Column(name = "to_name", length = 255)
    private String toName;

    @Column(name = "to_address", columnDefinition = "TEXT")
    private String toAddress;

    // Transporter Details
    @Column(name = "transporter_id", length = 50)
    private String transporterId;

    @Column(name = "transporter_name", length = 255)
    private String transporterName;

    @Column(name = "transporter_doc_no", length = 50)
    private String transporterDocNo;

    @Column(name = "transporter_doc_date")
    private LocalDate transporterDocDate;

    // Vehicle Details
    @Column(name = "vehicle_no", length = 20)
    private String vehicleNo;

    @Column(name = "vehicle_type", length = 1)
    private String vehicleType;

    @Column(name = "entered_date")
    private LocalDateTime enteredDate;

    @Column(name = "entered_by", length = 50)
    private String enteredBy;

    @Column(name = "cewb_no", length = 20)
    private String cewbNo;

    @Column(name = "multi_veh_info", columnDefinition = "TEXT")
    private String multiVehInfo;

    // Goods Details
    @OneToMany(mappedBy = "ewayBill", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<EWayBillGoodsDetail> goodsDetails;

    // Summary of Amounts
    @Column(name = "taxable_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxableAmount;

    @Column(name = "cgst_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "igst_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "cess_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cessAmount = BigDecimal.ZERO;

    @Column(name = "cess_non_advol_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cessNonAdvolAmount = BigDecimal.ZERO;

    @Column(name = "other_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal otherAmount = BigDecimal.ZERO;

    @Column(name = "total_invoice_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalInvoiceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EWayBillStatus status = EWayBillStatus.draft;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
