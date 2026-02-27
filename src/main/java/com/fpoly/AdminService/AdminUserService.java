package com.fpoly.AdminService;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.enums.VaiTro;
import com.fpoly.repository.NguoiDungRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lấy toàn bộ user
    public List<NguoiDung> getAllUsers() {
        return nguoiDungRepository.findAll();
    }

    // Lấy theo ID
    public NguoiDung getUserById(Long id) {
        return nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
    }

    // Save (add + update)
    public void saveUser(NguoiDung user) {

        if (user.getId() != null) {

            NguoiDung oldUser = nguoiDungRepository
                    .findById(user.getId())
                    .orElseThrow();

            // giữ password
            if (user.getMatKhau() == null || user.getMatKhau().isBlank()) {
                user.setMatKhau(oldUser.getMatKhau());
            } else {
                user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));
            }

            // giữ token
            if (user.getToken() == null) {
                user.setToken(oldUser.getToken());
            }

            if (user.getTrangThai() == null) {
                user.setTrangThai(oldUser.getTrangThai());
            }

        } else {

            user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));

            if (user.getTrangThai() == null) {
                user.setTrangThai("HOAT_DONG");
            }
        }

        nguoiDungRepository.save(user);
    }

    public void deleteUser(Long id) {
        nguoiDungRepository.deleteById(id);
    }
}
