package com.techacademy.service;

import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Employee> findAll() {
        return employeeRepository.findByDeleteFlgFalseOrderByCodeAsc();
    }

    public Employee findByCode(String code) {
        return employeeRepository.findById(code).orElse(null);
    }

    @Transactional
    public void save(Employee employee) {
        // deleteFlg は boolean で統一
        if (!employee.isDeleteFlg()) {
            // 何もしない（初期 false の想定）
        }

        // created/updated セット（なければ）
        LocalDateTime now = LocalDateTime.now();
        if (employee.getCreatedAt() == null) employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        // パスワードは平文なら暗号化（hashっぽいならそのまま）
        if (employee.getPassword() != null && !employee.getPassword().startsWith("$2a$")) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        employeeRepository.save(employee);
    }

    @Transactional
    public void logicalDelete(String code) {
        Employee employee = findByCode(code);
        if (employee == null) return;
        employee.setDeleteFlg(true);
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(employee);
    }
}
