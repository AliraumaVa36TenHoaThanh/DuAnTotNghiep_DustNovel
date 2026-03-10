package com.fpoly.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fpoly.model.AiDich;
import com.fpoly.model.Chuong;
import com.fpoly.repository.AiDichRepository;
import com.fpoly.repository.ChuongRepository;

@Service
public class ChuongDichService {

    @Autowired
    ChuongRepository chuongRepo;

    @Autowired
    AiDichRepository aiDichRepo;

    public String getTranslatedChapter(Long chuongId, String targetLang) {
        Chuong chuong = chuongRepo.findById(chuongId).orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));

        // 1. Kiểm tra trong DB (Cache) xem đã ai dịch chương này sang ngôn ngữ này chưa
        AiDich cachedTranslation = aiDichRepo.findByChuongAndNgonNgu(chuong, targetLang).orElse(null);
        if (cachedTranslation != null) {
            return cachedTranslation.getNoiDungDich(); // Có rồi thì trả về luôn (0 giây, 0 đồng)
        }

        // 2. Nếu chưa có, gọi API để dịch nội dung
        String originalHtml = chuong.getNoiDung();
        String translatedHtml = callGoogleTranslateAPI(originalHtml, targetLang);

        // 3. Lưu kết quả vào DB để lần sau không phải dịch lại
        AiDich newTranslation = new AiDich();
        newTranslation.setChuong(chuong);
        newTranslation.setNgonNgu(targetLang);
        newTranslation.setNoiDungDich(translatedHtml);
        newTranslation.setNgayTao(LocalDateTime.now());
        aiDichRepo.save(newTranslation);

        return translatedHtml;
    }

    // Hàm gọi API Dịch (Dùng URL miễn phí của Google)
    private String callGoogleTranslateAPI(String text, String targetLang) {
        try {
            // Mẹo dùng API ẩn của Google Translate (miễn phí)
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=vi&tl=" + targetLang + "&dt=t&q=" + text;
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            // Xử lý chuỗi JSON rườm rà của Google trả về (Lấy text đã dịch)
            String json = response.getBody();
            // Cắt chuỗi thô sơ (Có thể dùng thư viện Jackson/Gson để parse đẹp hơn)
            String[] parts = json.split("\"");
            if(parts.length > 1) {
                return parts[1];
            }
            return text; // Lỗi thì trả về bản gốc
        } catch (Exception e) {
            System.out.println("Lỗi dịch thuật: " + e.getMessage());
            return text; // Lỗi thì trả về bản gốc
        }
    }
}