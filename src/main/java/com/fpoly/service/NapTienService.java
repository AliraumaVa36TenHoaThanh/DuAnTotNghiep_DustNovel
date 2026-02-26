package com.fpoly.service;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NapTienService {

    private final NguoiDungRepository nguoiDungRepo;
    private final PasswordEncoder passwordEncoder;

    /* ===== LẤY USER ĐANG LOGIN ===== */
    public NguoiDung getByTenDangNhap(String tenDangNhap) {
        return nguoiDungRepo.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    /* ===== RANDOM 5 USER ===== */
    public List<NguoiDung> getRandomUsers() {
        return nguoiDungRepo.findRandom5Users();
    }

    /* ===== NẠP TOKEN ===== */
    @Transactional
    public void napToken(String tenDangNhap, Long soToken, String matKhau) {

        if (soToken == null || soToken <= 0)
            throw new RuntimeException("Số token không hợp lệ");

        NguoiDung user = getByTenDangNhap(tenDangNhap);

        if (!passwordEncoder.matches(matKhau, user.getMatKhau()))
            throw new RuntimeException("Mật khẩu không đúng");

        user.setToken(user.getToken() + soToken);
        nguoiDungRepo.save(user);
    }

    /* ===== TẶNG TOKEN ===== */
    @Transactional
    public void tangToken(String nguoiGui, String nguoiNhan, Long soToken, String matKhau) {

        if (soToken == null || soToken <= 0)
            throw new RuntimeException("Số token không hợp lệ");

        if (nguoiGui.equals(nguoiNhan))
            throw new RuntimeException("Không thể tự tặng cho chính mình");

        NguoiDung sender = getByTenDangNhap(nguoiGui);
        NguoiDung receiver = getByTenDangNhap(nguoiNhan);

        if (!passwordEncoder.matches(matKhau, sender.getMatKhau()))
            throw new RuntimeException("Mật khẩu không đúng");

        if (sender.getToken() < soToken)
            throw new RuntimeException("Không đủ token");

        sender.setToken(sender.getToken() - soToken);
        receiver.setToken(receiver.getToken() + soToken);

        nguoiDungRepo.save(sender);
        nguoiDungRepo.save(receiver);
    }
}
