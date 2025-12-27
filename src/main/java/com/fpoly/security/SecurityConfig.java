package com.fpoly.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
    	return NoOpPasswordEncoder.getInstance();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ❌ Tắt CSRF cho dễ test
            .csrf(csrf -> csrf.disable())

            // ✅ PHÂN QUYỀN
            .authorizeHttpRequests(auth -> auth

                // Static
                .requestMatchers(
                    "/DustNovel/css/**",
                    "/DustNovel/js/**",
                    "/DustNovel/images/**"
                ).permitAll()

                // Public
                .requestMatchers(
                    "/DustNovel/",
                    "/DustNovel/login",
                    "/DustNovel/truyen/**",
                    "/DustNovel/chuong/**"
                ).permitAll()

                // ADMIN
                .requestMatchers("/DustNovel/admin/**")
                .hasRole("ADMIN")

                // USER + ADMIN
                .requestMatchers(
                    "/DustNovel/user/**",
                    "/DustNovel/truyen/them",
                    "/DustNovel/truyen/luu"
                ).hasAnyRole("USER", "ADMIN")

                // Còn lại cần login
                .anyRequest().authenticated()
            )

            // ✅ LOGIN
            .formLogin(form -> form
                .loginPage("/DustNovel/login")
                .loginProcessingUrl("/DustNovel/login")
                .defaultSuccessUrl("/DustNovel/home", true)
                .failureUrl("/DustNovel/login?error")
                .permitAll()
            )

            // ✅ LOGOUT
            .logout(logout -> logout
                .logoutUrl("/DustNovel/logout")
                .logoutSuccessUrl("/DustNovel/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
