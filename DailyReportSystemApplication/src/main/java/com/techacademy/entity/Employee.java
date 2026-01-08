package com.techacademy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {

    public enum Role {
        ADMIN, GENERAL
    }

    @Id
    @Column(length = 10)
    @Size(max = 10, message = "従業員番号は10文字以内で入力してください。")
    private String code;

    @NotBlank(message = "氏名を入力してください。")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Role role;

    @NotBlank(message = "パスワードを入力してください。")
    private String password;

    @Column(name = "delete_flg", nullable = false)
    private boolean deleteFlg;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- getter / setter ---
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isDeleteFlg() { return deleteFlg; }
    public void setDeleteFlg(boolean deleteFlg) { this.deleteFlg = deleteFlg; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
