package com.fpoly.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.fpoly.service.CustomUserDetailsService;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            // ✅ GẮN USERDETAILSSERVICE Ở ĐÂY
            .userDetailsService(userDetailsService)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(	
                	"/DustNovel/home",
                    "/DustNovel/css/**",
                    "/DustNovel/js/**",
                    "/DustNovel/images/**",
                    "/DustNovel/login",
                    "/DustNovel/register",
                    "/DustNovel/truyen/**",
                    "/DustNovel/chuong/**"
                ).permitAll()

                .requestMatchers("/DustNovel/admin/**").hasRole("ADMIN")

                .requestMatchers(
                    "/DustNovel/user/**",
                    "/DustNovel/truyen/them",
                    "/DustNovel/truyen/luu"
                ).hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/DustNovel/login")
                .loginProcessingUrl("/DustNovel/login")
                .defaultSuccessUrl("/DustNovel/home", true)
                .failureUrl("/DustNovel/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/DustNovel/logout")
                .logoutSuccessUrl("/DustNovel/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
