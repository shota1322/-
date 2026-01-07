package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TopController {

    /**
     * トップ画面（/）
     * サイドバーのシステム名クリック時に呼ばれる
     */
    @GetMapping("/")
    public String top() {
        // 本来：日報一覧画面
        // return "redirect:/reports";

        // 現状：従業員一覧画面
        return "redirect:/employees";
    }

    /**
     * ログイン画面表示
     */
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    /**
     * ログイン処理
     */
    @PostMapping("/login")
    public String doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        // 仮の認証処理（本来はDB＋Service）
        if ("1001".equals(username) && "pass123".equals(password)) {
            // ログイン成功
            // 本来：日報一覧画面
            // return "redirect:/reports";

            // プロジェクト提供時：従業員一覧画面
            return "redirect:/employees";
        }

        // ログイン失敗
        return "redirect:/login?error";
    }
}
