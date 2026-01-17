package com.fpoly.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.security.CustomUserDetails;

@Controller
@RequestMapping("/DustNovel/user")
public class UserController {

    private final NguoiDungRepository nguoiDungRepo;
    private final PasswordEncoder passwordEncoder;

    public UserController(NguoiDungRepository nguoiDungRepo, PasswordEncoder passwordEncoder) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ===== HIỂN THỊ HỒ SƠ =====
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("user", userDetails.getUser());
        return "user/profile";
    }

    // ===== CẬP NHẬT HỒ SƠ =====
    @PostMapping("/profile/update")
    public String updateProfile(
            Authentication authentication,
            @RequestParam String tenDangNhap,
            @RequestParam String email,
            @RequestParam(required = false) String matKhauMoi,
            @RequestParam(required = false) String xacNhanMatKhau,
            Model model,
            RedirectAttributes redirectAttributes) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        NguoiDung user = userDetails.getUser();

        boolean hasError = false;

        // VALIDATE TÊN ĐĂNG NHẬP
        if (tenDangNhap == null || tenDangNhap.isBlank()) {
            model.addAttribute("tenDangNhapError", "Tên đăng nhập không được để trống");
            hasError = true;
        } else if (tenDangNhap.contains(" ")) {
            model.addAttribute("tenDangNhapError", "Tên đăng nhập không được chứa khoảng trắng");
            hasError = true;
        } else if (tenDangNhap.length() < 5) {
            model.addAttribute("tenDangNhapError", "Tên đăng nhập phải từ 5 ký tự trở lên");
            hasError = true;
        } else if (!tenDangNhap.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+$")) {
            model.addAttribute(
                    "tenDangNhapError",
                    "Tên đăng nhập phải chứa cả chữ và số, không chứa ký tự đặc biệt"
            );
            hasError = true;
        }

        // VALIDATE EMAIL
        if (email == null || email.isBlank()) {
            model.addAttribute("emailError", "Email không được để trống");
            hasError = true;
        } else if (!email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            model.addAttribute("emailError", "Email phải đúng định dạng @gmail.com");
            hasError = true;
        }

        // VALIDATE MẬT KHẨU
        if (matKhauMoi != null && !matKhauMoi.isBlank()) {
            if (!matKhauMoi.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                model.addAttribute(
                        "matKhauMoiError",
                        "Mật khẩu phải ≥ 8 ký tự, gồm chữ hoa, chữ thường và số"
                );
                hasError = true;
            }

            if (xacNhanMatKhau == null || xacNhanMatKhau.isBlank()) {
                model.addAttribute("xacNhanMatKhauError", "Vui lòng xác nhận mật khẩu");
                hasError = true;
            } else if (!matKhauMoi.equals(xacNhanMatKhau)) {
                model.addAttribute("xacNhanMatKhauError", "Xác nhận mật khẩu không khớp");
                hasError = true;
            }
        }

        // CÓ LỖI → GIỮ NGUYÊN DỮ LIỆU
        if (hasError) {
            model.addAttribute("user", user);
            model.addAttribute("tenDangNhapValue", tenDangNhap);
            model.addAttribute("emailValue", email);
            model.addAttribute("matKhauMoi", matKhauMoi);
            model.addAttribute("xacNhanMatKhau", xacNhanMatKhau);
            return "user/profile";
        }

        // LƯU
        user.setTenDangNhap(tenDangNhap);
        user.setEmail(email);

        if (matKhauMoi != null && !matKhauMoi.isBlank()) {
            user.setMatKhau(passwordEncoder.encode(matKhauMoi));
        }

        nguoiDungRepo.save(user);

        // ✅ FLASH ATTRIBUTE – CHỈ HIỆN 1 LẦN
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/DustNovel/user/profile";
    }

    // ===== XÓA TÀI KHOẢN =====
    @PostMapping("/delete")
    public String deleteAccount(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        nguoiDungRepo.deleteById(userDetails.getUser().getId());
        return "redirect:/DustNovel/logout";
    }
}
