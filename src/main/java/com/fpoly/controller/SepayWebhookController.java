package com.fpoly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fpoly.model.NapTienTuDong;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NapTienTuDongRepository;
import com.fpoly.repository.NguoiDungRepository;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class SepayWebhookController {

    @Autowired
    private NapTienTuDongRepository napTienRepo;
    
    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    @PostMapping("/sepay")
    public ResponseEntity<String> xuLyGiaoDich(@RequestBody Map<String, Object> payload) {
        try {
            String noiDungCK = (String) payload.get("content"); 
            Number transferAmountRaw = (Number) payload.get("transferAmount");
            long soTienChuyen = transferAmountRaw != null ? transferAmountRaw.longValue() : 0;

            if (noiDungCK == null || soTienChuyen <= 0) {
                return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
            }
            noiDungCK = noiDungCK.trim().toUpperCase();
            NapTienTuDong donNap = napTienRepo.findByNoiDungCk(noiDungCK);

            if (donNap != null && "PENDING".equals(donNap.getTrangThai())) {
              
                if (soTienChuyen >= donNap.getSoTienThuc()) {
                    
                    NguoiDung user = donNap.getNguoiDung();
                    user.setToken(user.getToken() + donNap.getSoTokenNhan());
                    nguoiDungRepo.save(user);
                    donNap.setTrangThai("SUCCESS");
                    napTienRepo.save(donNap);
                    
                    return ResponseEntity.ok("Xử lý thành công, đã cộng token!");
                } else {
                    donNap.setTrangThai("FAILED");
                    napTienRepo.save(donNap);
                    return ResponseEntity.ok("Giao dịch thất bại do chuyển thiếu tiền");
                }
            }
            
            return ResponseEntity.ok("Không tìm thấy đơn nạp hoặc đơn đã xử lý");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi server");
        }
    }
}