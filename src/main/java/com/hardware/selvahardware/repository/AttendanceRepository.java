package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Attendance;
import com.hardware.selvahardware.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(UUID employeeId, LocalDate date);

    List<Attendance> findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            UUID employeeId, LocalDate from, LocalDate to);

    List<Attendance> findByAttendanceDateBetweenOrderByAttendanceDateDesc(LocalDate from, LocalDate to);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :empId " +
           "AND a.attendanceDate BETWEEN :from AND :to AND a.status = :status")
    long countByEmployeeAndDateRangeAndStatus(@Param("empId") UUID empId,
                                              @Param("from") LocalDate from,
                                              @Param("to") LocalDate to,
                                              @Param("status") AttendanceStatus status);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :empId " +
           "AND a.attendanceDate BETWEEN :from AND :to")
    long countByEmployeeAndDateRange(@Param("empId") UUID empId,
                                     @Param("from") LocalDate from,
                                     @Param("to") LocalDate to);
}
