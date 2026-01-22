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
import java.util.List;
import java.util.NoSuchElementException;

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

        List<Report> reports = "ADMIN".equals(loginEmployee.getRole().name())
                ? reportService.findAllNotDeleted()
                : reportService.findByEmployeeNotDeleted(loginEmployee);

        model.addAttribute("reports", reports);
        return "reports/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        try {
            Report report = reportService.findByIdNotDeleted(id);
            model.addAttribute("report", report);
            return "reports/show";
        } catch (NoSuchElementException e) {
            return "redirect:/reports";
        }
    }

    @GetMapping("/new")
    public String newReport(Model model, Principal principal) {
        String loginCode = principal.getName();
        Employee employee = employeeService.findByCode(loginCode);

        model.addAttribute("report", new Report());
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

        if (result.hasErrors()) {
            model.addAttribute("employeeName", employee.getName());
            return "reports/new";
        }

        // 新規の重複チェック（同一社員・同日）
        if (reportService.existsNotDeleted(employee, report.getReportDate())) {
            model.addAttribute("employeeName", employee.getName());
            model.addAttribute("dateDuplicateError", "日報は既に登録されています");
            return "reports/new";
        }

        reportService.create(report, employee);
        return "redirect:/reports";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        try {
            Report report = reportService.findByIdNotDeleted(id);
            String employeeName = (report.getEmployee() != null) ? report.getEmployee().getName() : "";
            model.addAttribute("report", report);
            model.addAttribute("employeeName", employeeName);
            return "reports/edit";
        } catch (NoSuchElementException e) {
            return "redirect:/reports";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("report") Report input,
                         BindingResult result,
                         Model model,
                         Principal principal) {

        String loginCode = principal.getName();
        Employee loginEmployee = employeeService.findByCode(loginCode);

        // 編集対象の日報（これが「画面で表示中の日報」）
        Report target;
        try {
            target = reportService.findByIdNotDeleted(id);
        } catch (NoSuchElementException e) {
            return "redirect:/reports";
        }

        // 画面表示用の氏名は「編集対象の日報の従業員」
        String employeeName = (target.getEmployee() != null) ? target.getEmployee().getName() : "";

        if (result.hasErrors()) {
            model.addAttribute("employeeName", employeeName);
            input.setId(id);
            return "reports/edit";
        }

        try {
            // ★仕様どおりの重複チェックは service.update 内で target.getEmployee() 基準で行う
            reportService.update(id, input, loginEmployee);
            return "redirect:/reports/" + id;

        } catch (IllegalStateException e) {
            // ★同日重複エラー
            model.addAttribute("employeeName", employeeName);
            model.addAttribute("dateDuplicateError", "日報は既に登録されています");
            input.setId(id);
            return "reports/edit";

        } catch (NoSuchElementException e) {
            return "redirect:/reports";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        try {
            reportService.logicalDelete(id);
        } catch (NoSuchElementException e) {
            // 何もしない
        }
        return "redirect:/reports";
    }
}
