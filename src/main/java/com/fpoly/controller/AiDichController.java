package com.fpoly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fpoly.service.ChuongDichService;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/DustNovel/api/dich")
@RequiredArgsConstructor
public class AiDichController {

    private final ChuongDichService translateService;

    @PostMapping("/chuong")
    public ResponseEntity<?> dichChuong(@RequestBody Map<String, String> payload) {
        try {
            Long chuongId = Long.valueOf(payload.get("chuongId"));
            String lang = payload.get("lang"); // 'en', 'ja', 'zh'...

            String noiDungDaDich = translateService.getTranslatedChapter(chuongId, lang);
            return ResponseEntity.ok(noiDungDaDich);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi dịch thuật: " + e.getMessage());
        }
    }
}