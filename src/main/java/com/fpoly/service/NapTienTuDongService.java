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

        long soToken = soTienVnd / 20;

        NapTienTuDong don = new NapTienTuDong();
        don.setNguoiDung(user);
        
        DecimalFormat df = new DecimalFormat("#,###");
        don.setTenGoiNap("Gói " + df.format(soTienVnd) + " VNĐ");
        
        don.setSoTienThuc(soTienVnd);
        don.setSoTokenNhan(soToken);
        
        String noiDung = "DATN" + user.getId() + (System.currentTimeMillis() % 10000);
        don.setNoiDungCk(noiDung);
        don.setTrangThai("PENDING");

        return napTienTuDongRepo.save(don);
    }
}