package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.GstBillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GstBillItemRepository extends JpaRepository<GstBillItem, UUID> {

    List<GstBillItem> findByGstBillId(UUID gstBillId);

    void deleteByGstBillId(UUID gstBillId);
}
