package com.techacademy.repository;

import com.techacademy.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findByDeleteFlgOrderByCodeAsc(Integer deleteFlg);

    Optional<Employee> findByCodeAndDeleteFlg(String code, Integer deleteFlg);
}