package com.techacademy.repository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    // 管理者：全件（論理削除は @SQLRestriction で除外される）
    List<Report> findAllByOrderByReportDateDescCreatedAtDesc();

    // 一般：自分の分だけ
    List<Report> findByEmployeeOrderByReportDateDescCreatedAtDesc(Employee employee);

    // 詳細（JpaRepository の findById でもOKだけど、明示したいなら）
    Optional<Report> findById(Integer id);

    // 同一従業員 + 同一日付 の重複チェック（新規）
    boolean existsByEmployeeAndReportDate(Employee employee, LocalDate reportDate);

    // 同一従業員 + 同一日付 の重複チェック（更新：自分のidは除外）
    boolean existsByEmployeeAndReportDateAndIdNot(Employee employee, LocalDate reportDate, Integer id);
}
