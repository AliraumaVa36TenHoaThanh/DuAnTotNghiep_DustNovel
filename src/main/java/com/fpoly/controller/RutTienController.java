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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/DustNovel")
@RequiredArgsConstructor
public class RutTienController {
	
    private final NapTienService napTienService;
    private final RutTienRepository rutTienRepository;
    
    @GetMapping("/rut-tien")
    public String rutTien(Model model, Authentication auth) {

        var user = napTienService.getByTenDangNhap(auth.getName());

        model.addAttribute("title", "DustNovel | Rút tiền");
        model.addAttribute("content", "truyen/rut-tien"); 
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
}
