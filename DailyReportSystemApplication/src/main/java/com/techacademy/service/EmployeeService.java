package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 従業員保存処理
     */
    @Transactional
    public ErrorKinds save(Employee employee) {

        // パスワードチェック
        ErrorKinds result = employeePasswordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        // 社員番号重複チェック
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        employee.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    /**
     * 従業員削除処理（論理削除）
     */
    @Transactional
    public ErrorKinds delete(String code) {

        Employee employee = findByCode(code);

        // ※ 存在チェックは行わない（課題仕様）
        employee.setDeleteFlg(true);
        employee.setUpdatedAt(LocalDateTime.now());

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    /**
     * 従業員一覧取得
     */
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    /**
     * 従業員1件検索
     */
    public Employee findByCode(String code) {
        Optional<Employee> option = employeeRepository.findById(code);
        return option.orElse(null);
    }

    /**
     * パスワードチェック
     */
    private ErrorKinds employeePasswordCheck(Employee employee) {

        // 半角英数字チェック
        if (isHalfSizeCheckError(employee)) {
            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 8～16文字チェック
        if (isOutOfRangePassword(employee)) {
            return ErrorKinds.RANGECHECK_ERROR;
        }

        // パスワード暗号化
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        return ErrorKinds.CHECK_OK;
    }

    /**
     * 半角英数字チェック
     */
    private boolean isHalfSizeCheckError(Employee employee) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(employee.getPassword());
        return !matcher.matches();
    }

    /**
     * パスワード桁数チェック
     */
    public boolean isOutOfRangePassword(Employee employee) {
        int length = employee.getPassword().length();
        return length < 8 || length > 16;
    }
}
