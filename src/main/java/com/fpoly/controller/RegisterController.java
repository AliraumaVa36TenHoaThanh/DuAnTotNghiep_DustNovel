package com.fpoly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.model.NguoiDung;
import com.fpoly.service.RegisterService;

@Controller
@RequestMapping("/DustNovel")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    // HIỂN THỊ FORM
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "auth/register";
    }

    // XỬ LÝ ĐĂNG KÝ
    @PostMapping("/register")
    public String register(
        @ModelAttribute("nguoiDung") NguoiDung nguoiDung,
        @RequestParam("reMatKhau") String reMatKhau,
        Model model,
        RedirectAttributes redirectAttributes   // ✅ THÊM
    ) {
        String error = registerService.register(nguoiDung, reMatKhau);

        if (error != null) {
            model.addAttribute("error", error);
            return "auth/register";
        }

        // ✅ GỬI THÔNG BÁO SANG TRANG LOGIN
        redirectAttributes.addFlashAttribute(
            "success",
            "Đăng ký thành công! Vui lòng đăng nhập."
        );

        return "redirect:/DustNovel/login?success";
    }
}
