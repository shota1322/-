package com.techacademy.controller;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 従業員一覧画面表示
     */
    @GetMapping
    public String index(Model model) {
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "employees/index";
    }

    /**
     * 従業員新規登録画面表示
     */
    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("employee", new Employee());
        return "employees/new";
    }

    /**
     * 従業員新規登録処理
     */
    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute Employee employee,
            BindingResult result,
            Model model) {

        // 社員番号重複チェック
        if (employeeService.findByCode(employee.getCode()) != null) {
            model.addAttribute("codeError", "既に登録されている社員番号です");
            return "employees/new";
        }

        // パスワード未入力チェック
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            model.addAttribute("passwordError", "パスワードを入力してください");
            return "employees/new";
        }

        if (result.hasErrors()) {
            return "employees/new";
        }

        employeeService.save(employee);
        return "redirect:/employees";
    }

    /**
     * 従業員削除処理
     */
    @PostMapping("/{code}/delete")
    public String delete(@PathVariable String code) {
        employeeService.delete(code);
        return "redirect:/employees";
    }
}
