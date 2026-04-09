package com.fpoly.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fpoly.model.AiTomTat;
import com.fpoly.model.Chuong;
import com.fpoly.repository.AiTomTatRepository;
import com.fpoly.repository.ChuongRepository;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiTomTatService {

    private final AiTomTatRepository aiTomTatRepo;
    private final ChuongRepository chuongRepo;
    private final String GROQ_API_KEY = "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";

    public String getTomTatChuong(Long chuongId) {
        Chuong chuong = chuongRepo.findById(chuongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));

        AiTomTat cachedTomTat = aiTomTatRepo.findByChuong(chuong).orElse(null);

        if (cachedTomTat != null 
            && !cachedTomTat.getNoiDungTomTat().startsWith("AI lỗi")
            && !cachedTomTat.getNoiDungTomTat().contains("Hệ thống AI")) {
            
            return cachedTomTat.getNoiDungTomTat();
        }

        String noiDungGoc = chuong.getNoiDung();
        // Gọi hàm của Groq thay vì Gemini
        String tomTatMoi = callGroqToSummarize(noiDungGoc);

        AiTomTat tomTat;

        if (cachedTomTat != null) {
            tomTat = cachedTomTat;   // update bản cũ
        } else {
            tomTat = new AiTomTat(); // tạo mới
            tomTat.setChuong(chuong);
        }

        tomTat.setNoiDungTomTat(tomTatMoi);
        tomTat.setNgayTao(LocalDateTime.now());

        aiTomTatRepo.save(tomTat);

        return tomTatMoi;
    }

    // HÀM MỚI: GỌI GROQ API (LLaMA 3)
    private String callGroqToSummarize(String text) {
        try {
        	String url = "https://api.groq.com/openai/v1/chat/completions";
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(GROQ_API_KEY); // Groq dùng Bearer Token

            // Do API giới hạn độ dài, ta cắt bớt truyện nếu nó quá dài (lấy khoảng 5000 ký tự đầu để tóm tắt là đủ hiểu nội dung)
            String safeText = text.length() > 5000 ? text.substring(0, 5000) : text;

            String prompt = "Bạn là một biên tập viên truyện. Hãy tóm tắt nội dung chương truyện sau bằng tiếng Việt ngắn gọn. Giữ nguyên phiên âm tên nhân vật, không dịch tên. Nội dung: " + safeText;

            // Groq dùng cấu trúc JSON chuẩn của OpenAI
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-8b-instant");
            requestBody.put("messages", List.of(message));
            requestBody.put("temperature", 0.5);
            requestBody.put("max_tokens", 400);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Xử lý JSON trả về
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
            
            return (String) messageObj.get("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "AI lỗi: " + e.getMessage();
        }
    }
}