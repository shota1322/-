package com.techacademy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                // 認証不要
                .requestMatchers("/login").permitAll()
                .requestMatchers("/css/**", "/js/**").permitAll()

                // ★ 従業員は管理者のみ（UserDetailが ADMIN を持っているので hasAuthority）
                .requestMatchers("/employees/**").hasRole("ADMIN")


                // 日報はログインしていればOK
                .requestMatchers("/reports/**").authenticated()

                // その他もログイン必須
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/reports", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                 // POST /logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
