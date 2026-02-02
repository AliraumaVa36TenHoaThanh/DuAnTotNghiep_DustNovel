package com.fpoly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;

@Service
public class NguoiDungService {

    @Autowired
    private NguoiDungRepository repo;

    public void xoaTaiKhoan(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        repo.deleteById(id);
    }
    
    /* ===== THÊM / ĐỔI AVATAR ===== */
    public void luuAvatar(Long id, String avatar) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        repo.findById(id).ifPresent(u -> {
            u.setAvatar(avatar);
            repo.save(u);
        });
    }

    /* ===== XÓA AVATAR ===== */
    public void xoaAvatar(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        repo.findById(id).ifPresent(u -> {
            u.setAvatar(null);
            repo.save(u);
        });
    }
    
    /* ===== THÊM VÀ ĐỔI BANNER ===== */
    public void luuBanner(Long id, String banner) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        repo.findById(id).ifPresent(u -> {
            u.setBanner(banner);
            repo.save(u);
        });
    }

    /* ===== XÓA BANNER ===== */
    public void xoaBanner(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        repo.findById(id).ifPresent(u -> {
            u.setBanner(null);
            repo.save(u);
        });
    public NguoiDung findByTenDangNhap(String tenDangNhap) {
        return repo.findByTenDangNhap(tenDangNhap).orElse(null);
    }
}

