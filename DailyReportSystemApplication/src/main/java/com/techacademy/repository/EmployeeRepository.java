package com.techacademy.repository;

import com.techacademy.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findByDeleteFlgFalseOrderByCodeAsc();
}
