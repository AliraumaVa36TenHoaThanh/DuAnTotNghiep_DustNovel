package com.fpoly.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.enums.VaiTro;
import com.fpoly.repository.NguoiDungRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInit {

    @Bean
    CommandLineRunner initUser(
            NguoiDungRepository repo,
            PasswordEncoder encoder) {

        return args -> {

            if (repo.findByTenDangNhap("admin").isEmpty()) {
                NguoiDung u = new NguoiDung();
                u.setTenDangNhap("admin");
                u.setEmail("admin@gmail.com");
                u.setMatKhau(encoder.encode("123"));
                u.setVaiTro(VaiTro.ADMIN);
                u.setTrangThai("ACTIVE");

                repo.save(u);
            }
        };
    }
}
