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

	public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * 従業員保存（新規・更新共通）
	 */
	@Transactional
	public ErrorKinds save(Employee employee) {

		Employee existingEmployee = findByCode(employee.getCode());

		// 新規登録の場合のみ重複チェック
		if (existingEmployee == null) {

			// パスワード必須チェック
			if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
				return ErrorKinds.PASSWORD_EMPTY_ERROR;
			}
			// パスワード入力がある場合のみチェック＆暗号化
			if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
				ErrorKinds passwordResult = passwordCheck(employee);
				if (passwordResult != ErrorKinds.CHECK_OK) {
					return passwordResult;
				}
			}
		} else {
			// 更新時：パスワード未入力なら変更しない
			if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
				employee.setPassword(existingEmployee.getPassword());
			} else {
				// パスワード入力がある場合のみチェック＆暗号化
				if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
					ErrorKinds passwordResult = passwordCheck(employee);
					if (passwordResult != ErrorKinds.CHECK_OK) {
						return passwordResult;
					}
				}
			}
		}

//        // パスワード入力がある場合のみチェック＆暗号化
//        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
//            ErrorKinds passwordResult = passwordCheck(employee);
//            if (passwordResult != ErrorKinds.CHECK_OK) {
//                return passwordResult;
//            }

		LocalDateTime now = LocalDateTime.now();

		// 新規登録
		if (existingEmployee == null) {
			employee.setDeleteFlg(false);
			employee.setCreatedAt(now);
		} else {
			employee.setCreatedAt(existingEmployee.getCreatedAt());
		}

		employee.setUpdatedAt(now);

		employeeRepository.save(employee);
		return ErrorKinds.SUCCESS;
	}

	/**
	 * 従業員削除（論理削除）
	 */
	@Transactional
	public void delete(String code) {

		Employee employee = findByCode(code);

		if (employee == null) {
			return;
		}

		employee.setDeleteFlg(true);
		employee.setUpdatedAt(LocalDateTime.now());

		employeeRepository.save(employee);
	}

	/**
	 * 従業員一覧取得
	 */
	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}

	/**
	 * 従業員1件取得
	 */
	public Employee findByCode(String code) {

		Optional<Employee> option = employeeRepository.findById(code);
		return option.orElse(null);
	}

	/**
	 * パスワードチェック
	 */
	private ErrorKinds passwordCheck(Employee employee) {

		// 半角英数字チェック
		if (isHalfSizeCheckError(employee.getPassword())) {
			return ErrorKinds.HALFSIZE_ERROR;
		}

		// 桁数チェック
		if (isOutOfRangePassword(employee.getPassword())) {
			return ErrorKinds.RANGECHECK_ERROR;
		}

		// 暗号化
		employee.setPassword(passwordEncoder.encode(employee.getPassword()));
		return ErrorKinds.CHECK_OK;
	}

	/**
	 * 半角英数字チェック
	 */
	private boolean isHalfSizeCheckError(String password) {

		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
		Matcher matcher = pattern.matcher(password);
		return !matcher.matches();
	}

	/**
	 * 8～16文字チェック
	 */
	private boolean isOutOfRangePassword(String password) {

		int length = password.length();
		return length < 8 || length > 16;
	}
}
