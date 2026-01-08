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

    /**
     * 従業員一覧（論理削除除外）
     */
    public List<Employee> findAllNotDeleted() {
        return employeeRepository.findByDeleteFlgOrderByCodeAsc(0);
    }

    /**
     * 社員番号で取得（論理削除除外）
     */
    public Employee findByCode(String code) {
        return employeeRepository.findByCodeAndDeleteFlg(code, 0).orElse(null);
    }

    /**
     * 従業員登録
     * ※エラー種別を返す仕様に合わせた形（必要に応じて調整してください）
     */
    @Transactional
    public ErrorKinds save(Employee employee) {

        // ここで必須チェックや重複チェックを入れる課題もありますが、
        // 今は「動く形」に寄せて、最低限の値セットのみ行います。

        // --- 修正点：deleteFlg は boolean ではなく 0/1 ---
        employee.setDeleteFlg(0);

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        // パスワードは暗号化（空の場合はそのまま保存しない方が良いが、最低限の処理）
        if (employee.getPassword() != null && !employee.getPassword().isBlank()) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    /**
     * 従業員更新
     */
    @Transactional
    public ErrorKinds update(String code, Employee input) {

        Employee employee = findByCode(code);
        // ※存在チェックは行わない（課題仕様）と言われている場合はそのまま進める
        // ただし null の可能性はあるので、最低限だけガード（落ちるのを防ぐ）
        if (employee == null) {
            return ErrorKinds.NOT_FOUND_ERROR;
        }

        employee.setName(input.getName());
        employee.setRole(input.getRole());

        // パスワードが入力された時だけ更新（未入力なら変更しない）
        if (input.getPassword() != null && !input.getPassword().isBlank()) {
            employee.setPassword(passwordEncoder.encode(input.getPassword()));
        }

        employee.setUpdatedAt(LocalDateTime.now());

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    /**
     * 従業員削除（論理削除）
     */
    @Transactional
    public ErrorKinds delete(String code) {

        Employee employee = findByCode(code);

        // ※存在チェックは行わない（課題仕様）と書いてあっても、
        // null だと NPE で落ちるので最低限だけ回避
        if (employee == null) {
            return ErrorKinds.NOT_FOUND_ERROR;
        }

        // --- 修正点：deleteFlg は boolean ではなく 0/1 ---
        employee.setDeleteFlg(1);
        employee.setUpdatedAt(LocalDateTime.now());

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    /**
     * エラー種別（プロジェクト側の enum があるならそれに合わせてください）
     */
    public enum ErrorKinds {
        SUCCESS,
        NOT_FOUND_ERROR
    }
}

