package com.fpoly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.enums.VaiTro;
import com.fpoly.repository.NguoiDungRepository;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private NguoiDungRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {

        // ❌ Nếu đã có admin rồi thì không tạo nữa
        if (repo.findByTenDangNhap("admin").isPresent()) {
            return;
        }

        NguoiDung admin = new NguoiDung();
        admin.setTenDangNhap("admin");
        admin.setMatKhau(encoder.encode("123"));
        admin.setVaiTro(VaiTro.ADMIN);
        admin.setTrangThai("ACTIVE");

        repo.save(admin);

        System.out.println("✅ Đã tạo tài khoản admin / 123");
    }
}