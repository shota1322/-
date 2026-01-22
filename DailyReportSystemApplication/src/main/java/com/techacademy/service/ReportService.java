package com.techacademy.service;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 管理者：全件（論理削除は @SQLRestriction で除外される想定）
    public List<Report> findAllNotDeleted() {
        return reportRepository.findAllByOrderByReportDateDescCreatedAtDesc();
    }

    // 一般：自分の分だけ
    public List<Report> findByEmployeeNotDeleted(Employee employee) {
        return reportRepository.findByEmployeeOrderByReportDateDescCreatedAtDesc(employee);
    }

    // 詳細（存在しなければ例外）
    public Report findByIdNotDeleted(Integer id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Report not found. id=" + id));
    }

    // 新規作成（重複チェック付き）
    @Transactional
    public Report create(Report report, Employee employee) {

        // 同一従業員・同一日付の日報が既にある場合はエラー
        if (existsNotDeleted(employee, report.getReportDate())) {
            throw new IllegalStateException("同一日付の日報が既に存在します。");
        }

        report.setEmployee(employee);
        report.setDeleteFlg(false);

        if (report.getCreatedAt() == null) report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    // 更新（★仕様どおり：表示中の従業員 × 入力日付で重複チェック）
    @Transactional
    public Report update(Integer id, Report input, Employee loginEmployee) {

        Report target = findByIdNotDeleted(id);

        // ★重複チェック対象は「画面表示中の日報の従業員」
        Employee owner = target.getEmployee();

        // 更新時の重複チェック（自分のidは除外）
        if (existsNotDeleted(owner, input.getReportDate(), id)) {
            throw new IllegalStateException("同一日付の日報が既に存在します。");
        }

        target.setReportDate(input.getReportDate());
        target.setTitle(input.getTitle());
        target.setContent(input.getContent());
        target.setUpdatedAt(LocalDateTime.now());

        return reportRepository.save(target);
    }

    // 論理削除
    @Transactional
    public void logicalDelete(Integer id) {
        Report report = findByIdNotDeleted(id);
        report.setDeleteFlg(true);
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    // 重複チェック（新規）
    public boolean existsNotDeleted(Employee employee, LocalDate reportDate) {
        return reportRepository.existsByEmployeeAndReportDate(employee, reportDate);
    }

    // 重複チェック（更新：自分のIDは除外）
    public boolean existsNotDeleted(Employee employee, LocalDate reportDate, Integer id) {
        return reportRepository.existsByEmployeeAndReportDateAndIdNot(employee, reportDate, id);
    }
}
