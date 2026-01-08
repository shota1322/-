package com.techacademy.service;

import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<Report> findAllNotDeleted() {
        return reportRepository.findByDeleteFlgOrderByReportDateDescCreatedAtDesc((byte) 0);
    }

    public List<Report> findByEmployeeCodeNotDeleted(String employeeCode) {
        return reportRepository.findByEmployeeCodeAndDeleteFlgOrderByReportDateDescCreatedAtDesc(employeeCode, (byte) 0);
    }

    public Report findByIdNotDeleted(Integer id) {
        return reportRepository.findByIdAndDeleteFlg(id, (byte) 0).orElse(null);
    }

    public Report save(Report report) {
        return reportRepository.save(report);
    }

    public void logicalDelete(Integer id) {
        Report report = findByIdNotDeleted(id);
        if (report == null) return;
        report.setDeleteFlg((byte) 1);
        reportRepository.save(report);
    }

    public boolean existsNotDeleted(String employeeCode, LocalDate reportDate) {
        return reportRepository.existsByEmployeeCodeAndReportDateAndDeleteFlg(employeeCode, reportDate, (byte) 0);
    }
}
