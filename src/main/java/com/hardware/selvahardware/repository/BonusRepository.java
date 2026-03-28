package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, UUID> {

    List<Bonus> findByCustomer_IdOrderByCreatedAtDesc(UUID customerId);

    List<Bonus> findByCustomer_IdAndBonusMonthAndBonusYear(UUID customerId, Integer month, Integer year);

    @Query("SELECT b FROM Bonus b WHERE b.customer.id = :customerId AND b.bonusYear = :year ORDER BY b.bonusMonth DESC")
    List<Bonus> findByCustomerIdAndYear(@Param("customerId") UUID customerId, @Param("year") Integer year);

    @Query("SELECT b FROM Bonus b WHERE b.bonusYear = :year AND b.bonusMonth = :month ORDER BY b.createdAt DESC")
    List<Bonus> findByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT COALESCE(SUM(b.bonusAmount), 0) FROM Bonus b WHERE b.customer.id = :customerId AND b.bonusYear = :year")
    Double getTotalBonusForCustomerByYear(@Param("customerId") UUID customerId, @Param("year") Integer year);

    @Query("SELECT COALESCE(SUM(b.bonusAmount), 0) FROM Bonus b WHERE b.customer.id = :customerId AND b.bonusYear = :year AND b.bonusMonth = :month")
    Double getTotalBonusForCustomerByMonthAndYear(@Param("customerId") UUID customerId, @Param("year") Integer year, @Param("month") Integer month);

    boolean existsByCustomer_IdAndBonusMonthAndBonusYear(UUID customerId, Integer month, Integer year);
}
