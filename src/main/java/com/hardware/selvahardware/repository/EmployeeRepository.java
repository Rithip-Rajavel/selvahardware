package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    boolean existsByCode(String code);
    Optional<Employee> findByCode(String code);

    @Query("SELECT e FROM Employee e WHERE " +
           "(:active IS NULL OR e.isActive = :active) AND " +
           "(:search IS NULL OR e.name ILIKE CONCAT('%', cast(:search as text), '%') " +
           "OR e.code ILIKE CONCAT('%', cast(:search as text), '%') " +
           "OR e.phone LIKE CONCAT('%', cast(:search as text), '%'))")
    List<Employee> searchEmployees(@Param("active") Boolean active, @Param("search") String search);
}
