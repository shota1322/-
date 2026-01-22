package com.techacademy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false") // 論理削除されたデータを検索結果から除外
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

    // ★ ここが重要：employee_code を Employee に紐付ける（保存対象）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    // ★ 論理削除フラグ（TINYINT）
    @Column(name = "delete_flg", columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 作成・更新日時を自動セット（任意だけど便利）
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleteFlg = false;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ---- getter/setter ----
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public boolean isDeleteFlg() { return deleteFlg; }
    public void setDeleteFlg(boolean deleteFlg) { this.deleteFlg = deleteFlg; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
