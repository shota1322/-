package com.techacademy.controller;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 一覧
    @GetMapping
    public String list(Model model) {
        List<Employee> employeeList = employeeService.findAll();
        model.addAttribute("employeeList", employeeList);
        return "employees/list";
    }

    // 詳細（テストが /employees/1/ にアクセスするので両対応）
    @GetMapping({"/{code}", "/{code}/"})
    public String detail(@PathVariable String code, Model model) {
        Employee employee = employeeService.findByCode(code);
        model.addAttribute("employee", employee);
        return "employees/detail";
    }

    // 新規登録画面
    @GetMapping("/add")
    public String create(Model model) {
        model.addAttribute("employee", new Employee());
        return "employees/new";
    }

    // 新規登録処理
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("employee") Employee employee,
                      BindingResult result,
                      Model model) {

        if (result.hasErrors()) {
            return "employees/new";
        }

        // 初期は未削除
        employee.setDeleteFlg(false);

        employeeService.save(employee);
        return "redirect:/employees";
    }

    // 削除（論理削除）
    @PostMapping("/{code}/delete")
    public String delete(@PathVariable String code,
                         @AuthenticationPrincipal UserDetail userDetail,
                         Model model) {

        // ログイン中ユーザーを削除しようとしたら detail に戻す（テスト期待）
        if (userDetail != null && userDetail.getEmployee() != null
                && code.equals(userDetail.getEmployee().getCode())) {

            Employee employee = employeeService.findByCode(code);
            model.addAttribute("employee", employee);
            return "employees/detail";
        }

        employeeService.logicalDelete(code);
        return "redirect:/employees";
    }
}
