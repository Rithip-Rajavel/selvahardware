package com.hardware.selvahardware.dto.ewaybill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EWayBillRequest {

    @NotNull(message = "Invoice ID is required")
    private UUID invoiceId;

    private String ewayBillNo;
    private LocalDateTime generatedDate;
    private String generatedBy;
    private LocalDate validUpto;
    private String portal;

    // Transportation Details
    private String mode;
    private Integer approxDistance;
    private String type;
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private String transactionType;

    // FROM (Supplier)
    private String fromGstin;
    private String fromName;
    private String fromAddress;
    private String fromPlace;
    private String fromPincode;
    private String fromState;

    // TO (Recipient)
    private String toGstin;
    private String toName;
    private String toAddress;

    // Transporter Details
    private String transporterId;
    private String transporterName;
    private String transporterDocNo;
    private LocalDate transporterDocDate;

    // Vehicle Details
    private String vehicleNo;
    private String vehicleType;
    private LocalDateTime enteredDate;
    private String enteredBy;
    private String cewbNo;
    private String multiVehInfo;

    // Goods Details
    @Valid
    private List<EWayBillGoodsDetailDto> goodsDetails;

    // Summary of Amounts
    private BigDecimal taxableAmount;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;
    private BigDecimal cessAmount;
    private BigDecimal cessNonAdvolAmount;
    private BigDecimal otherAmount;
    private BigDecimal totalInvoiceAmount;
}
