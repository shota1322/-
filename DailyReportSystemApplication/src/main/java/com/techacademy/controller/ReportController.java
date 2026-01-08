package com.techacademy.controller;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String index(Model model, Principal principal) {
        String loginCode = principal.getName();
        Employee loginEmployee = employeeService.findByCode(loginCode);

        List<Report> reports = "ADMIN".equals(loginEmployee.getRole())
                ? reportService.findAllNotDeleted()
                : reportService.findByEmployeeCodeNotDeleted(loginCode);

        model.addAttribute("reports", reports);
        return "reports/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Report report = reportService.findByIdNotDeleted(id);
        if (report == null) return "redirect:/reports";
        model.addAttribute("report", report);
        return "reports/show";
    }

    @GetMapping("/new")
    public String newReport(Model model, Principal principal) {
        String loginCode = principal.getName();
        Employee employee = employeeService.findByCode(loginCode);

        Report report = new Report();
        report.setEmployeeCode(employee.getCode());

        model.addAttribute("report", report);
        model.addAttribute("employeeName", employee.getName());
        return "reports/new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("report") Report report,
                         BindingResult result,
                         Model model,
                         Principal principal) {

        String loginCode = principal.getName();
        Employee employee = employeeService.findByCode(loginCode);

        // 改ざん対策：ログインユーザーで固定
        report.setEmployeeCode(employee.getCode());

        if (result.hasErrors()) {
            model.addAttribute("employeeName", employee.getName());
            return "reports/new";
        }

        // 日付重複チェック（同一社員・同日・未削除）
        if (reportService.existsNotDeleted(employee.getCode(), report.getReportDate())) {
            model.addAttribute("employeeName", employee.getName());
            model.addAttribute("dateDuplicateError", "日報は既に登録されています");
            return "reports/new";
        }

        report.setDeleteFlg((byte) 0);
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());

        reportService.save(report);
        return "redirect:/reports";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Report report = reportService.findByIdNotDeleted(id);
        if (report == null) return "redirect:/reports";

        String employeeName = (report.getEmployee() != null) ? report.getEmployee().getName() : "";
        model.addAttribute("report", report);
        model.addAttribute("employeeName", employeeName);
        return "reports/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("report") Report input,
                         BindingResult result,
                         Model model) {

        Report report = reportService.findByIdNotDeleted(id);
        if (report == null) return "redirect:/reports";

        if (result.hasErrors()) {
            String employeeName = (report.getEmployee() != null) ? report.getEmployee().getName() : "";
            input.setId(id);
            input.setEmployeeCode(report.getEmployeeCode());
            model.addAttribute("employeeName", employeeName);
            return "reports/edit";
        }

        // 更新できる項目だけ更新
        report.setReportDate(input.getReportDate());
        report.setTitle(input.getTitle());
        report.setContent(input.getContent());
        report.setUpdatedAt(LocalDateTime.now());

        reportService.save(report);
        return "redirect:/reports/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        reportService.logicalDelete(id);
        return "redirect:/reports";
    }
}
