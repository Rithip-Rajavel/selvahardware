package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.PendingBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingBillRepository extends JpaRepository<PendingBill, String> {
}
