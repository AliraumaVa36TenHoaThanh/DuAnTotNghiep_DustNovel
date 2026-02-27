package com.fpoly.service;

import com.fpoly.model.NapTienTuDong;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NapTienTuDongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;

@Service
@RequiredArgsConstructor
public class NapTienTuDongService {

    private final NapTienTuDongRepository napTienTuDongRepo;

    public NapTienTuDong taoDonNap(NguoiDung user, long soTienVnd) {
        // Tỉ lệ: 20 VNĐ = 1 Token (=> 10.000 VNĐ = 500 Token)
        long soToken = soTienVnd / 20;

        NapTienTuDong don = new NapTienTuDong();
        don.setNguoiDung(user);
        
        // Định dạng số tiền cho đẹp (VD: 10,000 VNĐ) để lưu vào tên gói
        DecimalFormat df = new DecimalFormat("#,###");
        don.setTenGoiNap("Gói " + df.format(soTienVnd) + " VNĐ");
        
        don.setSoTienThuc(soTienVnd);
        don.setSoTokenNhan(soToken);
        
        // Sinh mã chuyển khoản duy nhất (Tiền tố DATN + ID User + Random 4 số)
        String noiDung = "DATN" + user.getId() + (System.currentTimeMillis() % 10000);
        don.setNoiDungCk(noiDung);
        don.setTrangThai("PENDING");

        return napTienTuDongRepo.save(don);
    }
}