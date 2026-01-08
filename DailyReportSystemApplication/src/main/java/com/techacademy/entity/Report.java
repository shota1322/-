package com.techacademy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "日付を入力してください。")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @NotBlank(message = "タイトルを入力してください。")
    @Size(max = 100, message = "タイトルは100文字以内で入力してください。")
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NotBlank(message = "内容を入力してください。")
    @Size(max = 600, message = "内容は600文字以内で入力してください。")
    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "employee_code", length = 10, nullable = false)
    private String employeeCode;

    @Column(name = "delete_flg", nullable = false)
    private Byte deleteFlg;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // employees を参照して氏名表示するため（表示専用）
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "employee_code",
            referencedColumnName = "code",
            insertable = false,
            updatable = false
    )
    private Employee employee;

    // ---- getter/setter ----
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public Byte getDeleteFlg() { return deleteFlg; }
    public void setDeleteFlg(Byte deleteFlg) { this.deleteFlg = deleteFlg; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}
