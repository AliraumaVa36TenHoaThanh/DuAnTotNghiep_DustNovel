package com.fpoly.controller;

import com.fpoly.repository.NapTienRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.NapTienService;
import com.fpoly.model.NapTien;
import com.fpoly.model.NguoiDung;
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
@RequestMapping("/DustNovel/nap-tien")
@RequiredArgsConstructor
public class NapTienController {

    private final NapTienService napTienService;
    
    @Autowired
    NapTienRepository napTienRepository;

    /* ========= TRANG NẠP / TẶNG ========= */
    @GetMapping
    public String napTien(Model model, Authentication auth) {

        var user = napTienService.getByTenDangNhap(auth.getName());

        model.addAttribute("title", "DustNovel | Nạp token");
        model.addAttribute("content", "truyen/nap-tien"); // fragment path

        model.addAttribute("user", user);
        model.addAttribute("randomUsers", napTienService.getRandomUsers());

        return "layout/main";
    }

    /* ========= NẠP TOKEN ========= */
    @PostMapping("/nap")
    public String hienQR(
            @RequestParam("soToken") Long soToken,
            Authentication auth,
            Model model
    ) {

        var user = napTienService.getByTenDangNhap(auth.getName());

        if (soToken == null || soToken <= 0) {
            model.addAttribute("error", "Số tiền không hợp lệ");
        } else {

            String noiDung = user.getTenDangNhap() + System.currentTimeMillis();

            model.addAttribute("soTien", soToken);
            model.addAttribute("noiDung", noiDung);
        }

        model.addAttribute("title", "DustNovel | Nạp token");
        model.addAttribute("content", "truyen/nap-tien");
        model.addAttribute("user", user);
        model.addAttribute("randomUsers", napTienService.getRandomUsers());

        return "layout/main";
    }

    @PostMapping("/xac-nhan")
    public String xacNhanNapTien(
            @RequestParam Long soTien,
            @RequestParam String noiDung,
            Authentication authentication
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        NguoiDung nguoiDung = userDetails.getUser(); // dùng NguoiDung

        NapTien nap = new NapTien();

        nap.setNguoiDung(nguoiDung);

        // set số tiền thực
        nap.setSoTienThuc(BigDecimal.valueOf(soTien));

        // ví dụ 1000đ = 1 token
        nap.setSoTokenNhan(soTien / 20);

        nap.setPhuongThuc("CHUYEN_KHOAN");

        nap.setTrangThai("PENDING");

        napTienRepository.save(nap);

        return "redirect:/DustNovel/nap-tien?success=Da gui yeu cau cho admin duyet";
    }
    
    /* ========= TẶNG TOKEN ========= */
    
    @GetMapping("/tang-tien")
    public String tangTien(Model model, Authentication auth) {

        var user = napTienService.getByTenDangNhap(auth.getName());

        model.addAttribute("title", "DustNovel | Nạp token");
        model.addAttribute("content", "truyen/tang-tien"); // fragment path

        model.addAttribute("user", user);
        model.addAttribute("randomUsers", napTienService.getRandomUsers());

        return "layout/main";
    }
    @PostMapping("/tang")
    public String tangToken(
            @RequestParam("nguoiNhan") String nguoiNhan,
            @RequestParam("soToken") Long soToken,
            @RequestParam("matKhau") String matKhau,
            Authentication auth,
            RedirectAttributes ra
    ) {
        try {
            napTienService.tangToken(auth.getName(), nguoiNhan, soToken, matKhau);
            ra.addFlashAttribute("success", "Tặng token thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/DustNovel/nap-tien";
    }
}
