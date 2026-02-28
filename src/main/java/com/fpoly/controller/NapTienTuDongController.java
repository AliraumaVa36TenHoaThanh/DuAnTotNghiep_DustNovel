package com.fpoly.controller;

import com.fpoly.model.NapTienTuDong;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NapTienTuDongRepository;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.NapTienTuDongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/DustNovel/nap-tien-tu-dong")
@RequiredArgsConstructor
public class NapTienTuDongController {

    private final NapTienTuDongService napTienService;
    private final NapTienTuDongRepository napTienRepo;
    private final SecurityUtil securityUtil;

    @GetMapping
    public String hienThiBaoGia(Model model) {
        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) return "redirect:/DustNovel/login";
        model.addAttribute("title", "DustNovel | Nạp tiền tự động");
        model.addAttribute("user", user);
        model.addAttribute("content", "naptien/chon-goi-nap");
        return "layout/main";
    }


    @PostMapping("/tao-don")
    public String taoDonNap(@RequestParam("soTienVnd") long soTienVnd) {
        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) return "redirect:/DustNovel/login";


        NapTienTuDong don = napTienService.taoDonNap(user, soTienVnd);
        return "redirect:/DustNovel/nap-tien-tu-dong/qr/" + don.getId();
    }

    @GetMapping("/qr/{id}")
    public String hienThiQR(@PathVariable Long id, Model model) {
        NapTienTuDong don = napTienRepo.findById(id).orElse(null);
        if (don == null || !don.getTrangThai().equals("PENDING")) {
            return "redirect:/DustNovel/nap-tien-tu-dong";
        }

        model.addAttribute("don", don);
        model.addAttribute("content", "naptien/qr-thanh-toan");
        return "layout/main";
    }
    @GetMapping("/api/check-status/{id}")
    @ResponseBody
    public String checkStatus(@PathVariable Long id) {
        NapTienTuDong don = napTienRepo.findById(id).orElse(null);
        return don != null ? don.getTrangThai() : "NOT_FOUND";
    }
}