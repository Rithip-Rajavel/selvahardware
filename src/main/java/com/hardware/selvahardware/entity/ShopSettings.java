package com.hardware.selvahardware.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopSettings {

    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    private String email;

    @Column(name = "gst_number", length = 50)
    private String gstNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "upi_id", length = 100)
    private String upiId;

    @Column(name = "default_gst_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal defaultGstPercentage = BigDecimal.valueOf(18);

    @Column(name = "invoice_prefix", length = 10)
    @Builder.Default
    private String invoicePrefix = "INV";

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
