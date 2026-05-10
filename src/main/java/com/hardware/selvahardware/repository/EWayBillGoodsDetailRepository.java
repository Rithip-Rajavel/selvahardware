package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.EWayBillGoodsDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EWayBillGoodsDetailRepository extends JpaRepository<EWayBillGoodsDetail, UUID> {

    List<EWayBillGoodsDetail> findByEwayBillId(UUID ewayBillId);

    void deleteByEwayBillId(UUID ewayBillId);
}
