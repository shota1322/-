package com.techacademy.controller;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ===== 一覧 =====
    @GetMapping
    public String list(Model model) {
        List<Employee> employeeList = employeeService.findAll();
        model.addAttribute("employeeList", employeeList);
        model.addAttribute("listSize", employeeList.size());
        return "employees/list";
    }

    // ===== 詳細 =====
    @GetMapping({"/{code}", "/{code}/"})
    public String detail(@PathVariable String code, Model model) {
        Employee employee = employeeService.findByCode(code);
        if (employee == null) {
            return "redirect:/employees";
        }
        model.addAttribute("employee", employee);
        return "employees/detail";
    }

    // ===== 新規登録画面 =====
    @GetMapping("/add")
    public String create(Model model) {
        model.addAttribute("employee", new Employee());
        return "employees/new";
    }

    // ===== 新規登録処理 =====
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("employee") Employee employee,
                      BindingResult result) {

        if (result.hasErrors()) {
            return "employees/new";
        }

        employee.setDeleteFlg(false);
        employeeService.save(employee);
        return "redirect:/employees";
    }

    // ===== 更新画面 =====
    @GetMapping("/{code}/edit")
    public String edit(@PathVariable String code, Model model) {
        Employee employee = employeeService.findByCode(code);
        if (employee == null) {
            return "redirect:/employees";
        }
        model.addAttribute("employee", employee);
        return "employees/edit";
    }

    // ===== 更新処理（★404対策・仕様どおり） =====
    @PostMapping("/{code}/update")
    public String update(@PathVariable String code,
                         @Valid @ModelAttribute("employee") Employee input,
                         BindingResult result) {

        if (result.hasErrors()) {
            return "employees/edit";
        }

        Employee target = employeeService.findByCode(code);
        if (target == null) {
            return "redirect:/employees";
        }

        // 更新対象のみ反映（社員番号は変更不可）
        target.setName(input.getName());
        target.setRole(input.getRole());

        // ★パスワードは「入力されたときだけ」更新
        if (input.getPassword() != null && !input.getPassword().isBlank()) {
            target.setPassword(input.getPassword()); // Service側でBCrypt
        }

        employeeService.save(target);
        return "redirect:/employees/" + code;
    }

    // ===== 削除（論理削除） =====
    @PostMapping("/{code}/delete")
    public String delete(@PathVariable String code,
                         @AuthenticationPrincipal UserDetail userDetail,
                         Model model) {

        // 自分自身は削除不可（仕様）
        if (userDetail != null
                && userDetail.getEmployee() != null
                && code.equals(userDetail.getEmployee().getCode())) {

            Employee employee = employeeService.findByCode(code);
            model.addAttribute("employee", employee);
            return "employees/detail";
        }

        employeeService.logicalDelete(code);
        return "redirect:/employees";
    }
}
