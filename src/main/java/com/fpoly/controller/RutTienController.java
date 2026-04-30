package com.fpoly.controller;

import com.fpoly.repository.NapTienRepository;
import com.fpoly.repository.RutTienRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.NapTienService;
import com.fpoly.model.NapTien;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.RutTien;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;



@Controller
@RequestMapping("/DustNovel")
@RequiredArgsConstructor
public class RutTienController {
	
    private final NapTienService napTienService;
    private final RutTienRepository rutTienRepository;
    
//    @GetMapping("/rut-tien")
//    public String rutTien(Model model, Authentication auth) {
//
//        var user = napTienService.getByTenDangNhap(auth.getName());
//
//        model.addAttribute("title", "DustNovel | Rút tiền");
//        model.addAttribute("content", "truyen/rut-tien"); // fragment path
//
//        model.addAttribute("user", user);
//
//        return "layout/main";
//    }
    
    @GetMapping("/rut-tien")
    public String rutTien(Model model, Authentication auth) {

        // Lấy người dùng hiện tại
        var user = napTienService.getByTenDangNhap(auth.getName());
        
        // Lấy danh sách yêu cầu rút tiền của người dùng
        var listRutTien = rutTienRepository.findByNguoiDungId(user.getId());

        // Gộp các yêu cầu theo ngân hàng + số tài khoản + tên chủ tài khoản
        Map<String, RutTien> grouped = new LinkedHashMap<>();
        for (RutTien rt : listRutTien) {
        	
            // key gồm ngân hàng + số tài khoản + tên chủ
            String key = rt.getNganHang() + "|" + rt.getSoTaiKhoan() + "|" + rt.getTenChuTaiKhoan();
            if (grouped.containsKey(key)) {
            	
                // Cộng dồn số token, thuế, token thực nhận
                RutTien existing = grouped.get(key);
                existing.setSoToken(existing.getSoToken() + rt.getSoToken());
                existing.setThue(existing.getThue() + rt.getThue());
                existing.setTokenThucNhan(existing.getTokenThucNhan() + rt.getTokenThucNhan());
                // Có thể update trạng thái nếu muốn, hoặc giữ trạng thái đầu tiên
                
            } else {
                grouped.put(key, rt);
            }
        }

        List<RutTien> listRutTienGop = new ArrayList<>(grouped.values());

        model.addAttribute("title", "DustNovel | Rút tiền");
        model.addAttribute("content", "truyen/rut-tien"); // fragment path
        model.addAttribute("listRutTien", listRutTienGop);
        model.addAttribute("user", user);

        return "layout/main";
    }
    
    @PostMapping("/rut-tien")
    public String xuLyRutTien(
            @RequestParam String nganHang,
            @RequestParam String soTaiKhoan,
            @RequestParam String tenChuTaiKhoan,
            @RequestParam Long soToken,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        var user = napTienService.getByTenDangNhap(auth.getName());

        // 🚨 Kiểm tra số dư
        if (soToken <= 0) {
            redirectAttributes.addFlashAttribute("error", "Số token không hợp lệ!");
            return "redirect:/DustNovel/rut-tien";
        }

        if (soToken > user.getToken()) {
            redirectAttributes.addFlashAttribute("error", 
                "Số token muốn rút lớn hơn số dư hiện tại!");
            return "redirect:/DustNovel/rut-tien";
        }

        Long thue = soToken / 10; // 10%
        Long thucNhan = soToken - thue;

        RutTien rutTien = new RutTien();
        rutTien.setNguoiDung(user);
        rutTien.setSoToken(soToken);
        rutTien.setThue(thue);
        rutTien.setTokenThucNhan(thucNhan);
        rutTien.setNganHang(nganHang);
        rutTien.setSoTaiKhoan(soTaiKhoan);
        rutTien.setTenChuTaiKhoan(tenChuTaiKhoan);

        rutTienRepository.save(rutTien);

        redirectAttributes.addFlashAttribute("success", "Đã gửi yêu cầu rút tiền!");
        return "redirect:/DustNovel/rut-tien";
    }
    
    
    @GetMapping("/rut-tien/{id}")
    public String xemChiTietRutTien(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Authentication auth) {

        var user = napTienService.getByTenDangNhap(auth.getName());

        RutTien rutTien = rutTienRepository.findById(id)
            .filter(rt -> rt.getNguoiDung().getId().equals(user.getId()))
            .orElseThrow(() -> new IllegalArgumentException("Yêu cầu rút tiền không tồn tại"));

        // Thêm dữ liệu vào redirect
        redirectAttributes.addFlashAttribute("nganHang", rutTien.getNganHang());
        redirectAttributes.addFlashAttribute("soTaiKhoan", rutTien.getSoTaiKhoan());
        redirectAttributes.addFlashAttribute("tenChuTaiKhoan", rutTien.getTenChuTaiKhoan());

        return "redirect:/DustNovel/rut-tien";
    }
    
    
    @PostMapping("/rut-tien/delete")
    @Transactional
    public String xoaTaiKhoan(
            @RequestParam Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        var user = napTienService.getByTenDangNhap(auth.getName());

        RutTien rutTien = rutTienRepository.findById(id)
                .orElse(null);

        if (rutTien == null || !rutTien.getNguoiDung().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản!");
            return "redirect:/DustNovel/rut-tien";
        }

 
        rutTienRepository.deleteByNganHangAndSoTaiKhoanAndTenChuTaiKhoan(
                rutTien.getNganHang(),
                rutTien.getSoTaiKhoan(),
                rutTien.getTenChuTaiKhoan()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tài khoản ngân hàng!");
        return "redirect:/DustNovel/rut-tien";
    }
    
}
