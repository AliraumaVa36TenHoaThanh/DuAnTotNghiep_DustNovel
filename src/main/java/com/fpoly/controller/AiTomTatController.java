package com.fpoly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fpoly.service.AiTomTatService;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/DustNovel/api/ai") // Vẫn giữ nguyên đường dẫn này để Javascript gọi trúng đích nhé
@RequiredArgsConstructor
public class AiTomTatController { // Đã đổi tên Class chuẩn theo ý ông

    private final AiTomTatService aiTomTatService;

    @PostMapping("/tom-tat")
    public ResponseEntity<?> tomTatChuong(@RequestBody Map<String, Long> payload) {
        try {
            // Lấy ID chương từ request của Javascript gửi lên
            Long chuongId = payload.get("chuongId");
            
            // Gọi Service xử lý
            String ketQua = aiTomTatService.getTomTatChuong(chuongId);
            
            return ResponseEntity.ok(ketQua);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tóm tắt: " + e.getMessage());
        }
    }
}