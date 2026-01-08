package com.techacademy.repository;

import com.techacademy.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    List<Report> findByDeleteFlgOrderByReportDateDescCreatedAtDesc(byte deleteFlg);

    List<Report> findByEmployeeCodeAndDeleteFlgOrderByReportDateDescCreatedAtDesc(
            String employeeCode, byte deleteFlg);

    Optional<Report> findByIdAndDeleteFlg(Integer id, byte deleteFlg);

    boolean existsByEmployeeCodeAndReportDateAndDeleteFlg(String employeeCode, LocalDate reportDate, byte deleteFlg);
}
