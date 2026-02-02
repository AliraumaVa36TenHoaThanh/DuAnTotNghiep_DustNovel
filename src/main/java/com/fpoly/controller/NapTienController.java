package com.fpoly.controller;

import com.fpoly.service.NapTienService;
import lombok.RequiredArgsConstructor;
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
    public String napToken(
            @RequestParam("soToken") Long soToken,
            @RequestParam("matKhau") String matKhau,
            Authentication auth,
            RedirectAttributes ra
    ) {
        try {
            napTienService.napToken(auth.getName(), soToken, matKhau);
            ra.addFlashAttribute("success", "Nạp token thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/DustNovel/nap-tien";
    }

    /* ========= TẶNG TOKEN ========= */
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
